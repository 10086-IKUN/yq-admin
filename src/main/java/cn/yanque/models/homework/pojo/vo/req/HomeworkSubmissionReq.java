package cn.yanque.models.homework.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学员提交作业请求。
 */
@Data
@Schema(description = "学员提交作业请求")
public class HomeworkSubmissionReq {
    /** 作业 ID。 */
    @NotNull(message = "作业ID不能为空")
    @Schema(description = "作业ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long assignmentId;

    /** 提交内容。 */
    @Schema(description = "提交内容")
    private String submitContent;

    /** 提交附件原文件名。 */
    @Schema(description = "提交附件名称")
    private String attachmentName;

    /** 提交附件 OSS objectKey。 */
    @Schema(description = "提交附件地址")
    private String attachmentUrl;

    /** 提交附件 MIME 类型。 */
    @Schema(description = "提交附件类型")
    private String attachmentType;
}
