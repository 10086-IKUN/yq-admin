package cn.yanque.models.system.role.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色分配权限响应")
public class RolePermissionAssignRes {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
