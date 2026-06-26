package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 学员批量保存答案的请求对象。
 */
@Data
@Schema(description = "答案批量保存请求")
public class ExamAnswerSaveReq {

    /** 当前页面需要保存的答案。 */
    @Valid
    @NotEmpty(message = "答案列表不能为空")
    @Schema(description = "答案列表")
    private List<ExamAnswerSaveItemReq> answers;
}
