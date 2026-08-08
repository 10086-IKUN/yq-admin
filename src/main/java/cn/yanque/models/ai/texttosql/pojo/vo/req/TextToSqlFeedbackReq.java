package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TextToSqlFeedbackReq {

    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    @NotBlank(message = "反馈结果不能为空")
    private String feedbackResult;

    private String errorType;

    @Size(max = 1000, message = "反馈说明不能超过1000字")
    private String comment;
}

