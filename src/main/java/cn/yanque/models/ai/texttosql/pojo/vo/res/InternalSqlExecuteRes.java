package cn.yanque.models.ai.texttosql.pojo.vo.res;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InternalSqlExecuteRes {

    private List<String> columns;

    private List<Map<String, Object>> rows;

    private Integer rowCount;

    private String executedSql;
}
