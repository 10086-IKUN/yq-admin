package cn.yanque.models.system.role.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统角色实体类
 * 对应数据库中的角色表
 */
@Data
public class SysRoleEntity {

    /** 角色ID */
    private Long id;
    /** 角色编码，系统内唯一 */
    private String roleCode;
    /** 角色名称 */
    private String roleName;
    /** 角色描述 */
    private String description;
    /** 状态，ACTIVE启用，INACTIVE禁用 */
    private String status;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
