package cn.yanque.models.interview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "doubao.asr")
public class DoubaoAsrProperties {
    private Boolean enabled = true;
    private String appKey;
    private String accessKey;
    private String apiKey;
    private String resourceId = "volc.bigasr.auc";
    private String submitUrl = "https://openspeech.bytedance.com/api/v3/auc/bigmodel/submit";
    private String queryUrl = "https://openspeech.bytedance.com/api/v3/auc/bigmodel/query";
    private Integer connectTimeoutSeconds = 5;
    private Integer requestTimeoutSeconds = 60;
    private Integer queryBatchSize = 10;
}
