package cn.yanque.models.ai.knowledge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yanque.ai-knowledge")
public class AiKnowledgeProperties {

    private String baseUrl = "http://localhost:8000";

    private String indexPath = "/api/knowledge/documents/index";

    private String searchPath = "/api/knowledge/search";

    private String chunksPath = "/api/knowledge/documents/chunks";

    private Integer connectTimeoutSeconds = 5;

    private Integer responseTimeoutSeconds = 180;

    private Boolean chatRagEnabled = true;

    private String chatKnowledgeBaseId = "course_all";

    private Integer chatTopK = 5;

    private Integer chatMaxCharsPerChunk = 900;

    private Integer chatMaxTotalChars = 3000;
}
