package cn.yanque.models.system.role.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色分配权限响应")

/**
 * RolePermissionAssignRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class RolePermissionAssignRes {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
