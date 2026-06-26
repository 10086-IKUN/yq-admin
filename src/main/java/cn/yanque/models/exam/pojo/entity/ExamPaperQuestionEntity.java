package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 试卷题目关联实体，对应 exam_paper_question。
 *
 * <p>题目内容来自 exam_question，分值和顺序属于当前试卷，
 * 因此必须放在关联表中。</p>
 */
@Data
@Schema(description = "试卷题目关联实体")
public class ExamPaperQuestionEntity {

    /** 关联记录主键。 */
    @Schema(description = "关联记录ID")
    private Long id;

    /** 试卷 ID。 */
    @Schema(description = "试卷ID")
    private Long paperId;

    /** 题目 ID。 */
    @Schema(description = "题目ID")
    private Long questionId;

    /** 当前试卷中该题的分值。 */
    @Schema(description = "题目分值")
    private BigDecimal questionScore;

    /** 当前试卷中的显示顺序。 */
    @Schema(description = "排序号")
    private Integer sortNum;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 联表字段：题型。 */
    @Schema(description = "题型")
    private String questionType;

    /** 联表字段：题干。 */
    @Schema(description = "题干")
    private String questionStem;

    /** 联表字段：选择题选项 JSON。 */
    @Schema(description = "选择题选项JSON")
    private String optionsJson;

    /** 联表字段：标准答案。仅在允许查看成绩和答案时返回学员端。 */
    @Schema(description = "标准答案")
    private String correctAnswer;

    /** 联表字段：答案解析。 */
    @Schema(description = "答案解析")
    private String answerAnalysis;
}
