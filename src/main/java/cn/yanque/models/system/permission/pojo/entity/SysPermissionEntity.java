package cn.yanque.models.system.permission.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统权限实体类
 * 对应数据库中的权限表
 */
@Data
public class SysPermissionEntity {

    /** 权限ID */
    private Long id;
    /** 父权限ID */
    private Long parentId;
    /** 权限名称 */
    private String permissionName;
    /** 权限类型 MENU/BUTTON/API */
    private String permissionType;
    /** 权限编码 */
    private String permissionCode;
    /** API路径 */
    private String apiPath;
    /** 图标 */
    private String icon;
    /** 排序 */
    private Integer sortNum;
    /** 状态 ACTIVE/INACTIVE */
    private String status;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
