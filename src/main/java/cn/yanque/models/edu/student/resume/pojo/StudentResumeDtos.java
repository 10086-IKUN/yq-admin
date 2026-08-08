package cn.yanque.models.edu.student.resume.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

public final class StudentResumeDtos {
    private StudentResumeDtos() {
    }

    @Data
    public static class SaveReq {
        @NotBlank(message = "简历文件不能为空")
        @Size(max = 500, message = "简历文件地址不能超过500个字符")
        private String objectKey;

        @NotBlank(message = "简历文件名不能为空")
        @Size(max = 255, message = "简历文件名不能超过255个字符")
        private String fileName;

        private Long fileSize;
    }

    @Data
    public static class Info {
        private Long studentId;
        private String studentName;
        private String objectKey;
        private String fileName;
        private Long fileSize;
        private Date uploadedAt;
        private String parseStatus;
        private String parseErrorMessage;
        private Date parsedAt;
        private String resumeText;
    }
}
