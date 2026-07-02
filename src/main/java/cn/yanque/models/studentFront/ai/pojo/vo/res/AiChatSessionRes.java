package cn.yanque.models.studentFront.ai.pojo.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

@Data

/**
 * AI 问答会话响应。
 *
 * <p>用于左侧会话列表和当前会话标题展示，包含消息数和最后更新时间。</p>
 */
public class AiChatSessionRes {

    private Long id;

    private String title;

    private String status;

    private Integer messageCount;

    private LocalDateTime lastMessageAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
