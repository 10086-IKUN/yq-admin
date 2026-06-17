package cn.yanque.models.homework.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作业发布实体。
 *
 * <p>对应数据库表：homework_assignment。
 * 这个实体既承接表字段，也承接 Mapper 联表查询出来的展示字段。</p>
 */
@Data
@Schema(description = "作业发布实体")
public class HomeworkAssignmentEntity {
    /** 主键 ID。 */
    @Schema(description = "作业ID")
    private Long id;

    /** 作业标题。 */
    @Schema(description = "作业标题")
    private String title;

    /** 作业说明，老师填写给学员看的要求。 */
    @Schema(description = "作业说明")
    private String description;

    /** 班级 ID，对应 edu_class.id。 */
    @Schema(description = "班级ID")
    private Long classId;

    /** 作业所属日期，例如第几天课程对应的日期。 */
    @Schema(description = "作业日期")
    private LocalDate homeworkDate;

    /** 课程 ID，对应 edu_course.id。 */
    @Schema(description = "课程ID")
    private Long courseId;

    /** 课程明细 ID，对应 edu_course_detail.id，可为空。 */
    @Schema(description = "课程明细ID")
    private Long courseDetailId;

    /** 发布作业时的课程内容快照，避免课程明细后续修改影响历史作业。 */
    @Schema(description = "课程内容快照")
    private String courseContentSnapshot;

    /** 作业附件原文件名。 */
    @Schema(description = "作业附件原文件名")
    private String attachmentName;

    /** 作业附件 OSS objectKey，不是永久访问 URL。 */
    @Schema(description = "作业附件OSS ObjectKey")
    private String attachmentUrl;

    /** 作业附件 MIME 类型。 */
    @Schema(description = "作业附件类型")
    private String attachmentType;

    /** 答案附件原文件名。 */
    @Schema(description = "答案附件原文件名")
    private String answerAttachmentName;

    /** 答案附件 OSS objectKey，不是永久访问 URL。 */
    @Schema(description = "答案附件OSS ObjectKey")
    private String answerAttachmentUrl;

    /** 答案附件 MIME 类型。 */
    @Schema(description = "答案附件类型")
    private String answerAttachmentType;

    /** 答案发布状态：UNPUBLISHED 未发布，PUBLISHED 已发布。 */
    @Schema(description = "答案发布状态", allowableValues = {"UNPUBLISHED", "PUBLISHED"})
    private String answerPublishStatus;

    /** 答案发布时间。 */
    @Schema(description = "答案发布时间")
    private LocalDateTime answerPublishTime;

    /** 发布答案的老师 ID。 */
    @Schema(description = "发布答案老师ID")
    private Long answerPublishTeacherId;

    /** 发布作业的老师 ID。 */
    @Schema(description = "发布作业老师ID")
    private Long publishTeacherId;

    /** 作业发布时间。 */
    @Schema(description = "作业发布时间")
    private LocalDateTime publishDate;

    /** 作业截止时间。 */
    @Schema(description = "作业截止时间")
    private LocalDateTime deadline;

    /** 作业状态：PUBLISHED 已发布，CLOSED 已关闭。 */
    @Schema(description = "作业状态", allowableValues = {"PUBLISHED", "CLOSED"})
    private String status;

    /** 是否允许学员重新提交。 */
    @Schema(description = "是否允许重交")
    private Boolean allowResubmit;

    /** 发布时班级应交人数快照。 */
    @Schema(description = "发布时班级应交人数快照")
    private Integer studentCountSnapshot;

    /** 是否删除：false 未删除，true 已删除。 */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /** 作业关闭时间。 */
    @Schema(description = "作业关闭时间")
    private LocalDateTime closeTime;

    /** 关闭作业的老师 ID。 */
    @Schema(description = "关闭作业老师ID")
    private Long closeTeacherId;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /** 联表查询字段：班级名称。 */
    @Schema(description = "班级名称")
    private String className;

    /** 联表查询字段：课程名称。 */
    @Schema(description = "课程名称")
    private String courseName;

    /** 联表查询字段：班级期数。 */
    @Schema(description = "班级期数")
    private Integer classTerm;

    /** 联表查询字段：发布作业老师姓名。 */
    @Schema(description = "发布作业老师姓名")
    private String publishTeacherName;

    /** 联表查询字段：发布答案老师姓名。 */
    @Schema(description = "发布答案老师姓名")
    private String answerPublishTeacherName;
}
