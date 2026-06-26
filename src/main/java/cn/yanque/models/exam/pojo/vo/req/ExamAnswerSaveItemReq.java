package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学员保存的一道题答案。
 */
@Data
@Schema(description = "单题答案保存项")
public class ExamAnswerSaveItemReq {

    /** 逐题答案记录 ID。 */
    @NotNull(message = "答题记录ID不能为空")
    @Schema(description = "答题记录ID")
    private Long answerId;

    /** 学员答案，未填写时可以为空。 */
    @Schema(description = "学员答案")
    private String answerContent;
}
