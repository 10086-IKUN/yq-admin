package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改权限请求")
public class PermissionUpdateReq extends PermissionCreateReq {

    @Schema(description = "权限ID")
    private Long id;
}
