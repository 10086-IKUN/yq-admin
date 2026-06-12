package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户角色关联实体类
 * 对应数据库中的用户角色关联表
 */
@Data
public class SysUserRoleEntity {

    /** ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 角色ID */
    private Long roleId;
    /** 创建时间 */
    private Date createdAt;
}
