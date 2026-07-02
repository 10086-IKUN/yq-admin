package cn.yanque.models.system.config.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

/**
 * ConfigUpdateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class ConfigUpdateReq {

    private Long id;

    @NotBlank(message = "配置Key不能为空")
    private String k;

    @NotBlank(message = "配置Value不能为空")
    private String v;
}
