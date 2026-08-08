package cn.yanque.models.ai.texttosql.service;

import cn.yanque.models.ai.texttosql.pojo.vo.req.InternalSqlExecuteReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.InternalSqlExecuteRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ReadOnlySqlExecutor {

    private static final int MAX_ROWS = 200;
    private static final int QUERY_TIMEOUT_SECONDS = 15;
    private static final Pattern SELECT_SQL = Pattern.compile("(?is)^select\\b");
    private static final Pattern FORBIDDEN_SQL = Pattern.compile(
            "(?is)(;|--|/\\*|\\*/|\\b(insert|update|delete|replace|merge|alter|drop|truncate|create|grant|revoke|call|execute)\\b|\\binto\\s+(out|dump)file\\b|\\bfor\\s+update\\b|\\block\\s+in\\s+share\\s+mode\\b|\\b(sleep|benchmark|load_file)\\s*\\()"
    );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public InternalSqlExecuteRes execute(InternalSqlExecuteReq request) {
        String sql = request.getSql().trim();
        if (!SELECT_SQL.matcher(sql).find() || FORBIDDEN_SQL.matcher(sql).find()) {
            throw new IllegalArgumentException("SQL 未通过后端只读校验，仅允许执行单条 SELECT 查询");
        }

        validateTables(sql, request.getUsedTables());

        QueryData data = jdbcTemplate.query(connection -> {
            var statement = connection.prepareStatement(sql);
            statement.setMaxRows(MAX_ROWS);
            statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            return statement;
        }, resultSet -> {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();
            List<String> columns = new ArrayList<>(columnCount);
            for (int index = 1; index <= columnCount; index++) {
                columns.add(metadata.getColumnLabel(index));
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int index = 1; index <= columnCount; index++) {
                    row.put(columns.get(index - 1), resultSet.getObject(index));
                }
                rows.add(row);
            }
            return new QueryData(columns, rows);
        });

        InternalSqlExecuteRes response = new InternalSqlExecuteRes();
        response.setColumns(data.columns());
        response.setRows(data.rows());
        response.setRowCount(data.rows().size());
        response.setExecutedSql(sql);
        return response;
    }

    private void validateTables(String sql, List<String> usedTables) {
        var matcher = Pattern.compile("(?is)\\b(?:from|join)\\s+`?([a-z_][a-z0-9_]*)`?").matcher(sql);
        var actualTables = new java.util.LinkedHashSet<String>();
        while (matcher.find()) {
            actualTables.add(matcher.group(1).toLowerCase());
        }
        var declaredTables = new java.util.LinkedHashSet<String>();
        if (usedTables != null) {
            usedTables.forEach(table -> declaredTables.add(table.toLowerCase()));
        }
        if (actualTables.isEmpty() || !declaredTables.containsAll(actualTables)) {
            throw new IllegalArgumentException("SQL 实际使用的数据表与 usedTables 不一致");
        }
    }

    private record QueryData(
            List<String> columns,
            List<Map<String, Object>> rows
    ) {
    }
}
