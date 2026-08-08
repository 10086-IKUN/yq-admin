package cn.yanque.models.interview.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InterviewQuestionBankEntity {
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
    private String vectorId;
    private BigDecimal confidence;
    private Date firstSeenAt;
    private Date lastSeenAt;
    private Long lastSourceTaskId;
    private Long lastSourceReviewId;
    private Date createdAt;
    private Date updatedAt;
}
