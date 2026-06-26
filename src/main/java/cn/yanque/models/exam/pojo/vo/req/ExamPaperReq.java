package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增或编辑试卷的请求对象。
 */
@Data
@Schema(description = "试卷保存请求")
public class ExamPaperReq {

    /** 试卷名称。 */
    @NotBlank(message = "试卷名称不能为空")
    @Schema(description = "试卷名称")
    private String paperName;

    /** 试卷说明。 */
    @Schema(description = "试卷说明")
    private String description;

    /** 建议考试时长。 */
    @NotNull(message = "考试时长不能为空")
    @Min(value = 1, message = "考试时长至少为1分钟")
    @Schema(description = "考试时长（分钟）")
    private Integer durationMinutes;

    /** 及格分。 */
    @NotNull(message = "及格分不能为空")
    @DecimalMin(value = "0", message = "及格分不能小于0")
    @Schema(description = "及格分")
    private BigDecimal passScore;

    /** 试卷状态。 */
    @NotBlank(message = "试卷状态不能为空")
    @Schema(description = "试卷状态", allowableValues = {"DRAFT", "ENABLED", "DISABLED"})
    private String status;

    /** 试卷题目配置。 */
    @Valid
    @NotEmpty(message = "试卷至少需要一道题")
    @Schema(description = "试卷题目")
    private List<ExamPaperQuestionReq> questions;
}
