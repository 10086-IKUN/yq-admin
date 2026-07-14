package cn.yanque.models.ai.texttosql.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Text-to-SQL 补充信息请求")
public class TextToSqlContinueReq {

    @NotBlank(message = "会话 ID 不能为空")
    private String conversationId;

    @NotBlank(message = "补充信息不能为空")
    private String userAnswer;
}
