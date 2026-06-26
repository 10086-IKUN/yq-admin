package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试试卷实体，对应 exam_paper。
 */
@Data
@Schema(description = "考试试卷实体")
public class ExamPaperEntity {

    /** 试卷主键。 */
    @Schema(description = "试卷ID")
    private Long id;

    /** 试卷名称。 */
    @Schema(description = "试卷名称")
    private String paperName;

    /** 考试说明。 */
    @Schema(description = "试卷说明")
    private String description;

    /** 建议考试时长，单位为分钟。 */
    @Schema(description = "考试时长（分钟）")
    private Integer durationMinutes;

    /** 试卷总分，由试卷题目分值自动汇总。 */
    @Schema(description = "试卷总分")
    private BigDecimal totalScore;

    /** 及格分。 */
    @Schema(description = "及格分")
    private BigDecimal passScore;

    /** 状态：DRAFT 草稿、ENABLED 启用、DISABLED 停用。 */
    @Schema(description = "试卷状态", allowableValues = {"DRAFT", "ENABLED", "DISABLED"})
    private String status;

    /** 创建老师 ID。 */
    @Schema(description = "创建老师ID")
    private Long createdBy;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 查询扩展字段：试卷题目数量。 */
    @Schema(description = "题目数量")
    private Integer questionCount;
}
