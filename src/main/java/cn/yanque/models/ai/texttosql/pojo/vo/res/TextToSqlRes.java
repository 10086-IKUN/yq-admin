package cn.yanque.models.ai.texttosql.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Text-to-SQL 处理结果")
public class TextToSqlRes {

    private String questionType;

    private String routeReason;

    private String finalAnswer;

    private String action;

    private String sql;

    private String reason;

    private List<String> missingInfo;

    private List<String> usedTables;

    private List<String> usedFields;

    private String conversationId;

    private Boolean interrupted;
}
