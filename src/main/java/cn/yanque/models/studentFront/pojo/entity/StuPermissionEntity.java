package cn.yanque.models.studentFront.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data

/**
 * StuPermissionEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
public class StuPermissionEntity {

    private Long id;
    private Long parentId;
    private String permissionCode;
    private String permissionName;
    private String permissionType;
    private String apiPath;
    private String routePath;
    private String icon;
    private Integer sortNum;
    private String description;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
