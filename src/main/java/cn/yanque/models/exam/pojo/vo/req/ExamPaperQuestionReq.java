package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 试卷中的单个题目配置。
 */
@Data
@Schema(description = "试卷题目配置")
public class ExamPaperQuestionReq {

    /** 题目 ID。 */
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID")
    private Long questionId;

    /** 该题在当前试卷中的分值。 */
    @NotNull(message = "题目分值不能为空")
    @DecimalMin(value = "0.01", message = "题目分值必须大于0")
    @Schema(description = "题目分值")
    private BigDecimal questionScore;

    /** 显示顺序。 */
    @NotNull(message = "题目顺序不能为空")
    @Schema(description = "排序号")
    private Integer sortNum;
}
