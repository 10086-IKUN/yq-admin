package cn.yanque.models.system.permission.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "权限分页响应")
public class PermissionPageRes {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限类型", allowableValues = {"MENU", "BUTTON", "API"})
    private String permissionType;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "API路径")
    private String apiPath;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortNum;

    @Schema(description = "状态", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private Date createdAt;
}
