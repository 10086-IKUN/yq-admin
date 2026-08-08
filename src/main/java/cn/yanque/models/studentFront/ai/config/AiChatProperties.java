package cn.yanque.models.studentFront.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yanque.ai-chat")

/**
 * AI 问答配置项。
 *
 * <p>从 yanque.ai-chat 前缀读取 Python 服务地址、SSE 路径、超时时间和上下文历史条数。</p>
 */
public class AiChatProperties {

    private String baseUrl = "http://localhost:8000";

    private String streamPath = "/api/ai-chat/stream";

    private String summarizePath = "/api/ai-chat/summarize";

    private Integer connectTimeoutSeconds = 5;

    private Integer responseTimeoutSeconds = 180;

    private Integer historyLimit = 20;

    private Boolean compressionEnabled = true;

    private Integer compressionTriggerMessageCount = 20;

    private Integer compressionTriggerChars = 12000;

    private Integer compressionKeepRecentMessages = 8;

    private Integer compressionBatchSize = 40;
}
