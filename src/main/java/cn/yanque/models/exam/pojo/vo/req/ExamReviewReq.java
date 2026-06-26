package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 老师批改主观题的请求对象。
 */
@Data
@Schema(description = "主观题批改请求")
public class ExamReviewReq {

    /** 本题得分。 */
    @NotNull(message = "得分不能为空")
    @DecimalMin(value = "0", message = "得分不能小于0")
    @Schema(description = "本题得分")
    private BigDecimal score;

    /** 批改意见。 */
    @Schema(description = "批改意见")
    private String reviewComment;
}
