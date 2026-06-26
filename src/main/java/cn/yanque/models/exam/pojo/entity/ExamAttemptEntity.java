package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员考试记录实体，对应 exam_attempt。
 */
@Data
@Schema(description = "学员考试记录实体")
public class ExamAttemptEntity {

    /** 考试记录主键。 */
    @Schema(description = "考试记录ID")
    private Long id;

    /** 考试发布 ID。 */
    @Schema(description = "考试发布ID")
    private Long scheduleId;

    /** 学员 ID。 */
    @Schema(description = "学员ID")
    private Long studentId;

    /** 开始考试时保存的学号快照。 */
    @Schema(description = "学号快照")
    private String studentNo;

    /** 开始考试时保存的姓名快照。 */
    @Schema(description = "姓名快照")
    private String studentNameSnapshot;

    /** 首次进入答题页的时间。 */
    @Schema(description = "开始答题时间")
    private LocalDateTime startTime;

    /** 最终交卷时间。 */
    @Schema(description = "交卷时间")
    private LocalDateTime submitTime;

    /** 状态：IN_PROGRESS、SUBMITTED、REVIEWED。 */
    @Schema(description = "考试记录状态", allowableValues = {"IN_PROGRESS", "SUBMITTED", "REVIEWED"})
    private String status;

    /** 客观题得分。 */
    @Schema(description = "客观题得分")
    private BigDecimal objectiveScore;

    /** 主观题得分。 */
    @Schema(description = "主观题得分")
    private BigDecimal subjectiveScore;

    /** 总得分。 */
    @Schema(description = "总得分")
    private BigDecimal totalScore;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 联表字段：考试名称。 */
    @Schema(description = "考试名称")
    private String examName;

    /** 联表字段：试卷总分。 */
    @Schema(description = "试卷总分")
    private BigDecimal paperTotalScore;
}
