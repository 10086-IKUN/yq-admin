package cn.yanque.models.homework.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交实体。
 *
 * <p>对应数据库表：homework_submission。
 * 记录学员提交作业的信息。</p>
 */
@Data
@Schema(description = "作业提交实体")
public class HomeworkSubmissionEntity {
    /** 主键 ID。 */
    @Schema(description = "提交ID")
    private Long id;

    /** 作业 ID，对应 homework_assignment.id。 */
    @Schema(description = "作业ID")
    private Long assignmentId;

    /** 学员编号。 */
    @Schema(description = "学员编号")
    private String studentNo;

    /** 学员姓名快照。 */
    @Schema(description = "学员姓名快照")
    private String studentNameSnapshot;

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

    /** 提交时间。 */
    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    /** 状态：SUBMITTED 已提交，REVIEWED 已批阅，RESUBMIT_REQUIRED 需要重交。 */
    @Schema(description = "状态", allowableValues = {"SUBMITTED", "REVIEWED", "RESUBMIT_REQUIRED"})
    private String status;

    /** 得分。 */
    @Schema(description = "得分")
    private BigDecimal score;

    /** 老师评语。 */
    @Schema(description = "老师评语")
    private String teacherComment;

    /** 批改老师 ID。 */
    @Schema(description = "批改老师ID")
    private Long reviewTeacherId;

    /** 批改时间。 */
    @Schema(description = "批改时间")
    private LocalDateTime reviewTime;

    /** 提交版本号。 */
    @Schema(description = "提交版本号")
    private Integer version;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
