package cn.yanque.models.interview.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InterviewQuestionSourceEntity {
    private Long id;
    private Long questionId;
    private Long taskId;
    private Long reviewRecordId;
    private String originalQuestion;
    private String answerContext;
    private String studentAnswerQuality;
    private Integer studentAnswerScore;
    private String studentAnswerAnalysis;
    private String improvementSuggestion;
    private BigDecimal confidence;
    private String sourceHash;
    private String companyName;
    private String interviewRole;
    private Date interviewTime;
    private Date createdAt;
}
