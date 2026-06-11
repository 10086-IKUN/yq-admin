package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除用户响应")
public class UserDeleteRes {

    @Schema(description = "用户ID")
    private Long id;
}
