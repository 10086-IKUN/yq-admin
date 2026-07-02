package cn.yanque.models.system.user.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改用户请求")

/**
 * UserUpdateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class UserUpdateReq extends UserCreateReq {

    @Schema(description = "用户ID")
    private Long id;
}
