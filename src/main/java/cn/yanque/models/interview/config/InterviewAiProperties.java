package cn.yanque.models.interview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yanque.ai-interview")
public class InterviewAiProperties {
    private String baseUrl = "http://localhost:8000";
    private String polishPath = "/api/interview-reviews/dialogue/polish";
    private String reportPath = "/api/interview-reviews/report/generate";
    private String questionExtractPath = "/api/interview-reviews/questions/extract";
    private String questionVectorSearchPath = "/api/interview-reviews/questions/vector/search";
    private String questionVectorUpsertPath = "/api/interview-reviews/questions/vector/upsert";
    private String mockInterviewProfileGeneratePath = "/api/interview-reviews/mock-interview/profile/generate";
    private Integer connectTimeoutSeconds = 5;
    private Integer responseTimeoutSeconds = 180;
}
