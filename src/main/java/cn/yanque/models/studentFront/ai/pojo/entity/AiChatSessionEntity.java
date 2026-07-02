package cn.yanque.models.studentFront.ai.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data

/**
 * AI 问答会话实体。
 *
 * <p>对应 ai_chat_session 表，记录学生会话、会话状态、消息统计、摘要和后续归档信息。</p>
 */
public class AiChatSessionEntity {

    private Long id;

    private Long studentId;

    private String studentCode;

    private String title;

    private String status;

    private Integer messageCount;

    private LocalDateTime lastMessageAt;

    private String summary;

    /** 会话摘要 token 数，后续做长上下文压缩时使用。 */
    private Integer summaryTokenCount;

    private Long compressedUntilMessageId;

    private String archiveStatus;

    private Long archiveId;

    private LocalDateTime expireAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
