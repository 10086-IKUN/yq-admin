package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 角色权限关联实体类
 * 对应数据库中的角色权限关联表
 */
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
