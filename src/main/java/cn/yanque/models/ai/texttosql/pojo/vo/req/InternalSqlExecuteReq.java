package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class InternalSqlExecuteReq {

    @NotBlank(message = "SQL 不能为空")
    private String sql;

    private List<String> usedTables;
}
