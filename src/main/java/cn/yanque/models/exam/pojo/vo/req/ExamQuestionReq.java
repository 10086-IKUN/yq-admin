package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增或编辑题目的请求对象。
 */
@Data
@Schema(description = "题目保存请求")
public class ExamQuestionReq {

    /** 题型。 */
    @NotBlank(message = "题型不能为空")
    @Schema(description = "题型", allowableValues = {"SINGLE", "MULTIPLE", "JUDGE", "FILL", "SHORT"})
    private String questionType;

    /** 题干。 */
    @NotBlank(message = "题干不能为空")
    @Schema(description = "题干")
    private String questionStem;

    /** 选择题选项 JSON 数组。 */
    @Schema(description = "选择题选项JSON")
    private String optionsJson;

    /** 标准答案。 */
    @Schema(description = "标准答案")
    private String correctAnswer;

    /** 答案解析。 */
    @Schema(description = "答案解析")
    private String answerAnalysis;

    /** 难度。 */
    @NotBlank(message = "难度不能为空")
    @Schema(description = "难度", allowableValues = {"EASY", "MEDIUM", "HARD"})
    private String difficulty;

    /** 状态。 */
    @NotNull(message = "题目状态不能为空")
    @Schema(description = "状态：1启用，0停用")
    private Integer status;
}
