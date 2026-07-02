package cn.yanque.models.system.permission.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改权限请求")

/**
 * PermissionUpdateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class PermissionUpdateReq extends PermissionCreateReq {

    @Schema(description = "权限ID")
    private Long id;
}
