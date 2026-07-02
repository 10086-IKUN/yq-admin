package cn.yanque.models.system.role.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页查询角色请求")

/**
 * RolePageReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class RolePageReq {

    @Schema(description = "关键词（角色编码/角色名称）")
    private String keyword;

    @Schema(description = "状态", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
