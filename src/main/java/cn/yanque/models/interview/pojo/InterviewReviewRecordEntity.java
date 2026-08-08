package cn.yanque.models.interview.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class InterviewReviewRecordEntity {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private String companyName;
    private String interviewRole;
    private Date interviewTime;
    private String audioObjectKey;
    private String audioFileName;
    private Long audioFileSize;
    private String resumeText;
    private String reviewRemark;
    private String status;
    private String transcriptTaskId;
    private String transcriptDialogueJson;
    private String reportJson;
    private String errorMessage;
    private String questionStatus;
    private Integer questionExtractedCount;
    private Integer questionCreatedCount;
    private Integer questionMergedCount;
    private String questionFailReason;
    private Date startedAt;
    private Date completedAt;
    private Long createdBy;
    private Date createdAt;
    private Date updatedAt;
}
