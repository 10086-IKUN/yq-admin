package cn.yanque.models.exam.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试题目实体。
 *
 * <p>对应数据库表 exam_question。选择题选项使用 JSON 字符串保存，
 * 这样单选题和多选题不需要额外维护一张选项表。</p>
 */
@Data
@Schema(description = "考试题目实体")
public class ExamQuestionEntity {

    /** 题目主键。 */
    @Schema(description = "题目ID")
    private Long id;

    /** 题型：SINGLE 单选、MULTIPLE 多选、JUDGE 判断、FILL 填空、SHORT 简答。 */
    @Schema(description = "题型", allowableValues = {"SINGLE", "MULTIPLE", "JUDGE", "FILL", "SHORT"})
    private String questionType;

    /** 题干正文。 */
    @Schema(description = "题干")
    private String questionStem;

    /** 选择题选项 JSON 数组，非选择题为空。 */
    @Schema(description = "选择题选项JSON")
    private String optionsJson;

    /** 标准答案，多选答案使用英文逗号分隔。 */
    @Schema(description = "标准答案")
    private String correctAnswer;

    /** 答案解析，成绩可见后展示给学员。 */
    @Schema(description = "答案解析")
    private String answerAnalysis;

    /** 难度：EASY、MEDIUM、HARD。 */
    @Schema(description = "难度", allowableValues = {"EASY", "MEDIUM", "HARD"})
    private String difficulty;

    /** 是否启用。 */
    @Schema(description = "状态：1启用，0停用")
    private Integer status;

    /** 创建老师 ID。 */
    @Schema(description = "创建老师ID")
    private Long createdBy;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
