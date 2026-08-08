package cn.yanque.models.studentFront.ai.pojo.vo.res;

import lombok.Data;

/**
 * AI 对话上下文压缩结果。
 */
@Data
public class AiChatCompressionRes {

    private Boolean compressed;

    private Integer compressedMessageCount;

    private Integer keptRecentMessageCount;

    private Long compressedUntilMessageId;

    private Integer summaryTokenCount;

    private String message;
}
