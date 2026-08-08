package cn.yanque.models.ai.prompt.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptTemplateReq {
    @NotBlank(message = "提示词编码不能为空")
    @Pattern(regexp = "[a-z0-9_.-]{2,100}", message = "编码只能包含小写字母、数字、点、横线和下划线")
    private String code;

    @NotBlank(message = "提示词名称不能为空")
    @Size(max = 100, message = "提示词名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "提示词说明不能超过500个字符")
    private String description;

    private String status = "ACTIVE";

    /** 新建模板时作为 v1 内容；修改模板基本信息时可不传。 */
    private String content;

    @Size(max = 500, message = "版本备注不能超过500个字符")
    private String remark;
}
