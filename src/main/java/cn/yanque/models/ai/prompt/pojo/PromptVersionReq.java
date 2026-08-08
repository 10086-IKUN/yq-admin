package cn.yanque.models.ai.prompt.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptVersionReq {
    @NotBlank(message = "提示词内容不能为空")
    private String content;

    @Size(max = 500, message = "版本备注不能超过500个字符")
    private String remark;
}
