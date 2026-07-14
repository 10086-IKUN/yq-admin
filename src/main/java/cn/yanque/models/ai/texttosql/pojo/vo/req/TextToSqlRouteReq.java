package cn.yanque.models.ai.texttosql.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Text-to-SQL 查询请求")
public class TextToSqlRouteReq {

    @NotBlank(message = "查询问题不能为空")
    @Schema(description = "自然语言查询问题")
    private String userQuestion;

    @Schema(description = "会话 ID；首次查询可不传")
    private String conversationId;
}
