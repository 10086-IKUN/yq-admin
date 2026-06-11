package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SysRolePermissionEntity {

    /** ID */
    private Long id;
    /** 角色ID */
    private Long roleId;
    /** 权限ID */
    private Long permissionId;
    /** 创建时间 */
    private Date createdAt;
}
