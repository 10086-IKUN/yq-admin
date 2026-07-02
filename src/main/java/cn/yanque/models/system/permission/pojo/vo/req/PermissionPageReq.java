package cn.yanque.models.system.permission.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页查询权限请求")

/**
 * PermissionPageReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class PermissionPageReq {

    @Schema(description = "关键词（权限名称/权限编码）")
    private String keyword;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "权限类型", allowableValues = {"MENU", "BUTTON", "API"})
    private String permissionType;

    @Schema(description = "状态", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
