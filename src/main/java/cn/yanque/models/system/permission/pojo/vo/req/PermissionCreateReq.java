package cn.yanque.models.system.permission.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建权限请求")

/**
 * PermissionCreateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class PermissionCreateReq {

    @NotNull(message = "父权限ID不能为空")
    @Schema(description = "父权限ID")
    private Long parentId;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称")
    private String permissionName;

    @NotBlank(message = "权限类型不能为空")
    @Schema(description = "权限类型", allowableValues = {"MENU", "BUTTON", "API"})
    private String permissionType;

    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "API路径")
    private String apiPath;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortNum;

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Schema(description = "描述")
    private String description;
}
