package cn.yanque.models.system.user.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求")

/**
 * LoginReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
