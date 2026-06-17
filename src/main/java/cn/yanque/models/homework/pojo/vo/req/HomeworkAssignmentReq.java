package cn.yanque.models.homework.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作业发布和编辑请求。
 *
 * <p>发布作业和编辑作业共用这个请求体。
 * 前端会在点击“确定”后才上传附件，所以这里收到的附件字段都是最终要保存的值。</p>
 */
@Data
@Schema(description = "作业发布和编辑请求")
public class HomeworkAssignmentReq {
    /** 作业标题，必填。 */
    @NotBlank(message = "作业标题不能为空")
    @Schema(description = "作业标题")
    private String title;

    /** 作业说明，可为空。 */
    @Schema(description = "作业说明")
    private String description;

    /** 班级 ID，必填。 */
    @NotNull(message = "班级不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    /** 作业日期，可为空。 */
    @Schema(description = "作业日期")
    private LocalDate homeworkDate;

    /** 课程 ID，必填。 */
    @NotNull(message = "课程不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    /** 课程明细 ID，可为空。 */
    @Schema(description = "课程明细ID")
    private Long courseDetailId;

    /** 课程内容快照，通常由课程明细自动带出，也允许老师手动调整。 */
    @Schema(description = "课程内容快照")
    private String courseContentSnapshot;

    /** 作业附件原文件名。 */
    @Schema(description = "作业附件原文件名")
    private String attachmentName;

    /** 作业附件 OSS objectKey。 */
    @Schema(description = "作业附件OSS ObjectKey")
    private String attachmentUrl;

    /** 作业附件 MIME 类型。 */
    @Schema(description = "作业附件类型")
    private String attachmentType;

    /** 作业截止时间。 */
    @Schema(description = "作业截止时间")
    private LocalDateTime deadline;

    /** 是否允许学员重新提交。 */
    @Schema(description = "是否允许重交")
    private Boolean allowResubmit;
}
