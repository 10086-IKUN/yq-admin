package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TextToSqlEvalRunClarifyReq {

    /** 针对某条待澄清评测结果的补充回答，会使用原 conversationId 调用 continue。 */
    @NotBlank(message = "澄清补充内容不能为空")
    private String userAnswer;
}

