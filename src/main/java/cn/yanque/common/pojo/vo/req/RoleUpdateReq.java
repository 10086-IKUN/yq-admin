package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改角色请求")
public class RoleUpdateReq extends RoleCreateReq {

    @Schema(description = "角色ID")
    private Long id;
}
