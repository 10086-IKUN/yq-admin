package cn.yanque.models.studentFront.ai.pojo.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

@Data

/**
 * AI 问答消息响应。
 *
 * <p>前端按 role 区分学生消息和助手消息，并用 contentType 决定展示为纯文本还是 Markdown。</p>
 */
public class AiChatMessageRes {

    private Long id;

    private Long sessionId;

    private String role;

    private String content;

    private String contentType;

    private Integer tokenCount;

    private String modelName;

    private String finishReason;

    private LocalDateTime createdAt;
}
