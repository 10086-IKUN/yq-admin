package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试发布实体，对应 exam_schedule。
 */
@Data
@Schema(description = "考试发布实体")
public class ExamScheduleEntity {

    /** 考试发布记录主键。 */
    @Schema(description = "考试发布ID")
    private Long id;

    /** 使用的试卷 ID。 */
    @Schema(description = "试卷ID")
    private Long paperId;

    /** 参加考试的班级 ID。 */
    @Schema(description = "班级ID")
    private Long classId;

    /** 学员端展示的考试名称。 */
    @Schema(description = "考试名称")
    private String examName;

    /** 允许开始答题的时间。 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 考试截止时间，超过后不允许继续提交。 */
    @Schema(description = "截止时间")
    private LocalDateTime endTime;

    /** 状态：PUBLISHED、CLOSED、CANCELLED。 */
    @Schema(description = "考试状态", allowableValues = {"PUBLISHED", "CLOSED", "CANCELLED"})
    private String status;

    /** 老师是否提前发布答案。 */
    @Schema(description = "是否发布答案")
    private Boolean answerPublished;

    /** 老师发布答案的时间。 */
    @Schema(description = "答案发布时间")
    private LocalDateTime answerPublishTime;

    /** 发布老师 ID。 */
    @Schema(description = "发布老师ID")
    private Long createdBy;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 联表字段：试卷名称。 */
    @Schema(description = "试卷名称")
    private String paperName;

    /** 联表字段：班级期数。 */
    @Schema(description = "班级期数")
    private Integer classTerm;

    /** 联表字段：试卷总分。 */
    @Schema(description = "试卷总分")
    private BigDecimal totalScore;

    /** 联表字段：考试时长。 */
    @Schema(description = "考试时长（分钟）")
    private Integer durationMinutes;

    /** 联表字段：已经参加考试的人数。 */
    @Schema(description = "已参加人数")
    private Integer attemptCount;

    /** 当前学员的考试记录 ID，未开始时为空。 */
    @Schema(description = "当前学员考试记录ID")
    private Long attemptId;

    /** 当前学员考试状态。 */
    @Schema(description = "当前学员考试状态")
    private String attemptStatus;

    /** 当前学员总得分，成绩不可见时不返回。 */
    @Schema(description = "当前学员得分")
    private BigDecimal studentScore;

    /** 当前时间是否允许学员查看成绩和标准答案。 */
    @Schema(description = "成绩是否可见")
    private Boolean resultVisible;
}
