package cn.yanque.models.interview.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public final class InterviewQuestionDtos {
    private InterviewQuestionDtos() {}

    @Data
    public static class PageReq {
        private Integer pageNum = 1;
        private Integer pageSize = 20;
        private String keyword;
        private String category;
        private String auditStatus;
        private Boolean frequentOnly;
    }

    @Data
    public static class Item {
        private Long id;
        private String normalizedQuestion;
        private String category;
        private String tags;
        private String standardAnswer;
        private String shortAnswer;
        private String followUpQuestions;
        private String pitfalls;
        private Integer sourceCount;
        private String auditStatus;
        private String vectorStatus;
        private BigDecimal confidence;
        private Date firstSeenAt;
        private Date lastSeenAt;
        private Long lastSourceReviewId;
        private Date createdAt;
        private Date updatedAt;
    }

    @Data
    public static class AuditReq {
        @NotBlank
        private String auditStatus;
    }

    @Data
    public static class SourceItem {
        private Long id;
        private Long reviewRecordId;
        private String originalQuestion;
        private String answerContext;
        private String studentAnswerQuality;
        private Integer studentAnswerScore;
        private String studentAnswerAnalysis;
        private String improvementSuggestion;
        private BigDecimal confidence;
        private String companyName;
        private String interviewRole;
        private Date interviewTime;
        private Date createdAt;
    }

    @Data
    public static class Detail {
        private Item question;
        private List<SourceItem> sources;
    }
}
