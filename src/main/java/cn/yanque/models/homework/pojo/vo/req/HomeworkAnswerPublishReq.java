package cn.yanque.models.homework.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发布答案附件请求。
 *
 * <p>老师在作业管理页面点击“答案”后提交。
 * 当前设计为单个答案附件，所以这里不使用附件数组。</p>
 */
@Data
@Schema(description = "发布答案附件请求")
public class HomeworkAnswerPublishReq {
    /** 答案附件原文件名，必填。 */
    @NotBlank(message = "答案附件名称不能为空")
    @Schema(description = "答案附件原文件名")
    private String answerAttachmentName;

    /** 答案附件 OSS objectKey，必填。 */
    @NotBlank(message = "答案附件地址不能为空")
    @Schema(description = "答案附件OSS ObjectKey")
    private String answerAttachmentUrl;

    /** 答案附件 MIME 类型。 */
    @Schema(description = "答案附件类型")
    private String answerAttachmentType;
}
