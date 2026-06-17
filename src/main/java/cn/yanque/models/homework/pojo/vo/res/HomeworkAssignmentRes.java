package cn.yanque.models.homework.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作业列表和详情响应。
 *
 * <p>列表页和编辑弹窗详情共用这个响应对象。
 * 里面既包含作业表字段，也包含班级、课程、老师姓名等展示字段。</p>
 */
@Data
@Schema(description = "作业列表和详情响应")
public class HomeworkAssignmentRes {
    /** 作业 ID。 */
    @Schema(description = "作业ID")
    private Long id;

    /** 作业标题。 */
    @Schema(description = "作业标题")
    private String title;

    /** 作业说明。 */
    @Schema(description = "作业说明")
    private String description;

    /** 班级 ID。 */
    @Schema(description = "班级ID")
    private Long classId;

    /** 班级名称。 */
    @Schema(description = "班级名称")
    private String className;

    /** 班级期数。 */
    @Schema(description = "班级期数")
    private Integer classTerm;

    /** 作业日期。 */
    @Schema(description = "作业日期")
    private LocalDate homeworkDate;

    /** 课程 ID。 */
    @Schema(description = "课程ID")
    private Long courseId;

    /** 课程名称。 */
    @Schema(description = "课程名称")
    private String courseName;

    /** 课程明细 ID。 */
    @Schema(description = "课程明细ID")
    private Long courseDetailId;

    /** 发布作业时保存的课程内容快照。 */
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

    /** 答案附件原文件名。 */
    @Schema(description = "答案附件原文件名")
    private String answerAttachmentName;

    /** 答案附件 OSS objectKey。 */
    @Schema(description = "答案附件OSS ObjectKey")
    private String answerAttachmentUrl;

    /** 答案附件 MIME 类型。 */
    @Schema(description = "答案附件类型")
    private String answerAttachmentType;

    /** 答案发布状态。 */
    @Schema(description = "答案发布状态", allowableValues = {"UNPUBLISHED", "PUBLISHED"})
    private String answerPublishStatus;

    /** 答案发布时间。 */
    @Schema(description = "答案发布时间")
    private LocalDateTime answerPublishTime;

    /** 发布答案老师姓名。 */
    @Schema(description = "发布答案老师姓名")
    private String answerPublishTeacherName;

    /** 发布作业老师 ID。 */
    @Schema(description = "发布作业老师ID")
    private Long publishTeacherId;

    /** 发布作业老师姓名。 */
    @Schema(description = "发布作业老师姓名")
    private String publishTeacherName;

    /** 作业发布时间。 */
    @Schema(description = "作业发布时间")
    private LocalDateTime publishDate;

    /** 作业截止时间。 */
    @Schema(description = "作业截止时间")
    private LocalDateTime deadline;

    /** 作业状态。 */
    @Schema(description = "作业状态", allowableValues = {"PUBLISHED", "CLOSED"})
    private String status;

    /** 是否允许重交。 */
    @Schema(description = "是否允许重交")
    private Boolean allowResubmit;

    /** 发布时班级应交人数快照。 */
    @Schema(description = "发布时班级应交人数快照")
    private Integer studentCountSnapshot;

    /** 作业关闭时间。 */
    @Schema(description = "作业关闭时间")
    private LocalDateTime closeTime;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
