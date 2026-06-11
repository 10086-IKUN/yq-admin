package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改角色响应")
public class RoleUpdateRes {

    @Schema(description = "角色ID")
    private Long id;
}
