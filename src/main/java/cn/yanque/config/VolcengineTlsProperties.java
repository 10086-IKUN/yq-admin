package cn.yanque.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "volcengine.tls")
public class VolcengineTlsProperties {

    private String endpoint;

    private String region = "cn-beijing";

    private String accessKeyId;

    private String accessKeySecret;

    private Integer connectTimeoutMs = 5000;

    private Integer readTimeoutMs = 10000;
}
