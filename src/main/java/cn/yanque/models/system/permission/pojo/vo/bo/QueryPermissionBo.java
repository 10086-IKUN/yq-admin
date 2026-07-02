package cn.yanque.models.system.permission.pojo.vo.bo;

import lombok.Data;

import java.util.List;

@Data

/**
 * QueryPermissionBo 业务查询对象。
 *
 * <p>用于在服务层或 Mapper 层之间传递组合查询条件。</p>
 */
public class QueryPermissionBo {

    private Long id;
    private Long parentId;
    private String permissionName;
    private String permissionType;
    private String permissionCode;
    private String apiPath;
    private String icon;
    private Integer sortNum;
    private String status;
    private String description;
    private List<Long> ids;
}
