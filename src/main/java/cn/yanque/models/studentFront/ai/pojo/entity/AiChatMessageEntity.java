package cn.yanque.models.studentFront.ai.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data

/**
 * AI 问答消息实体。
 *
 * <p>对应 ai_chat_message 表，保存学生输入、助手回复、模型信息、token 数和后续检索引用。</p>
 */
public class AiChatMessageEntity {

    private Long id;

    private Long sessionId;

    private Long studentId;

    private String role;

    private String content;

    private String contentType;

    private Integer tokenCount;

    private Long retrievalId;

    private String sourceRefsJson;

    private String modelName;

    private String finishReason;

    /** 标记该消息是否已经被压缩进会话摘要，当前普通问答阶段默认 false。 */
    private Boolean compressed;

    private LocalDateTime createdAt;
}
