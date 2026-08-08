package cn.yanque.models.loganalysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yanque.ai-log-diagnosis")
public class LogDiagnosisProperties {

    private String baseUrl = "http://localhost:8000";

    private String contextPath = "/api/log-diagnosis/context";

    private String analyzePath = "/api/log-diagnosis/analyze";

    private Integer connectTimeoutSeconds = 5;

    private Integer responseTimeoutSeconds = 180;
}
