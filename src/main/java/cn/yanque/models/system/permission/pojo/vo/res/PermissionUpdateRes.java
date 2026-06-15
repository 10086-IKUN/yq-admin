package cn.yanque.models.system.permission.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改权限响应")
public class PermissionUpdateRes {

    @Schema(description = "权限ID")
    private Long id;
}
