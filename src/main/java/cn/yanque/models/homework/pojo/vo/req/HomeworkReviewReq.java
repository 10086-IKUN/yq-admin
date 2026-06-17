package cn.yanque.models.homework.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 批改作业请求。
 */
@Data
@Schema(description = "批改作业请求")
public class HomeworkReviewReq {
    /** 得分。 */
    @Schema(description = "得分")
    private BigDecimal score;

    /** 老师评语。 */
    @Schema(description = "老师评语")
    private String teacherComment;
}
