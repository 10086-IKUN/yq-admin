package cn.yanque.models.studentFront.ai.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

/**
 * AI 问答发送请求。
 *
 * <p>sessionId 为空时表示开启新会话；不为空时表示在已有会话中继续追问。</p>
 */
public class AiChatSendReq {

    /** 继续对话的会话 ID；为空时由后端自动创建新会话。 */
    private Long sessionId;

    /** 学生本轮输入的问题。 */
    @NotBlank(message = "请输入问题")
    private String message;
}
