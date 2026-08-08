package cn.yanque.models.interview.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

public final class InterviewReviewDtos {
    private InterviewReviewDtos() {
    }

    @Data
    public static class CreateReq {
        private Long studentId;
        private String companyName;
        private String interviewRole;
        @NotNull(message = "面试时间不能为空")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date interviewTime;
        @NotBlank(message = "录音文件不能为空")
        private String audioObjectKey;
        @NotBlank(message = "录音文件名不能为空")
        private String audioFileName;
        private Long audioFileSize;
        private String resumeText;
        private String reviewRemark;
    }

    @Data
    public static class PageReq {
        private Integer pageNum = 1;
        private Integer pageSize = 20;
        private Long studentId;
        private String keyword;
        private String status;
    }

    @Data
    public static class Item {
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
        private String reviewRemark;
        private String status;
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
        private Date createdAt;
        private Date updatedAt;
    }
}
