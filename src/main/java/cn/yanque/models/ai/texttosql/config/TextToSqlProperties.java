package cn.yanque.models.ai.texttosql.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yanque.ai-text-to-sql")
public class TextToSqlProperties {

    private String baseUrl = "http://localhost:8000";

    private String routePath = "/api/text-to-sql/route";

    private String continuePath = "/api/text-to-sql/continue";

    private Integer connectTimeoutSeconds = 5;

    private Integer responseTimeoutSeconds = 180;
}
