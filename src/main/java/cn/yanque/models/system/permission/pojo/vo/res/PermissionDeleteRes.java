package cn.yanque.models.system.permission.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除权限响应")
public class PermissionDeleteRes {

    @Schema(description = "权限ID")
    private Long id;
}
