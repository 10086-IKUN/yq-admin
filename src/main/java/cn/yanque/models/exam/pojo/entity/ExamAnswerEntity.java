package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员逐题答案实体，对应 exam_answer。
 */
@Data
@Schema(description = "学员逐题答案实体")
public class ExamAnswerEntity {

    /** 答题记录主键。 */
    @Schema(description = "答题记录ID")
    private Long id;

    /** 所属考试记录 ID。 */
    @Schema(description = "考试记录ID")
    private Long attemptId;

    /** 试卷题目关联 ID，用于确定题目顺序和分值。 */
    @Schema(description = "试卷题目关联ID")
    private Long paperQuestionId;

    /** 原题目 ID。 */
    @Schema(description = "题目ID")
    private Long questionId;

    /** 学员填写的答案。 */
    @Schema(description = "学员答案")
    private String answerContent;

    /** 客观题是否正确，主观题保持为空。 */
    @Schema(description = "是否正确")
    private Boolean correct;

    /** 本题最终得分。 */
    @Schema(description = "本题得分")
    private BigDecimal score;

    /** 老师批改意见。 */
    @Schema(description = "批改意见")
    private String reviewComment;

    /** 批改老师 ID。 */
    @Schema(description = "批改老师ID")
    private Long reviewTeacherId;

    /** 批改时间。 */
    @Schema(description = "批改时间")
    private LocalDateTime reviewTime;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 联表字段：题型。 */
    @Schema(description = "题型")
    private String questionType;

    /** 联表字段：题干。 */
    @Schema(description = "题干")
    private String questionStem;

    /** 联表字段：选项 JSON。 */
    @Schema(description = "选项JSON")
    private String optionsJson;

    /** 联表字段：标准答案。 */
    @Schema(description = "标准答案")
    private String correctAnswer;

    /** 联表字段：答案解析。 */
    @Schema(description = "答案解析")
    private String answerAnalysis;

    /** 联表字段：本题满分。 */
    @Schema(description = "本题满分")
    private BigDecimal questionScore;

    /** 联表字段：题目顺序。 */
    @Schema(description = "排序号")
    private Integer sortNum;
}
