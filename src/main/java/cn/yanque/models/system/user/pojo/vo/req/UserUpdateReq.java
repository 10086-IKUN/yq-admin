package cn.yanque.models.system.user.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改用户请求")
public class UserUpdateReq extends UserCreateReq {

    @Schema(description = "用户ID")
    private Long id;
}
