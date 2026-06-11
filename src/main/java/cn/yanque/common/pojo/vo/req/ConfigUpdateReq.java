package cn.yanque.common.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigUpdateReq {

    private Long id;

    @NotBlank(message = "配置Key不能为空")
    private String k;

    @NotBlank(message = "配置Value不能为空")
    private String v;
}
