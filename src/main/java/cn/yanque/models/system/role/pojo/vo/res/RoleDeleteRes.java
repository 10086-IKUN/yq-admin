package cn.yanque.models.system.role.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除角色响应")
public class RoleDeleteRes {

    @Schema(description = "角色ID")
    private Long id;
}
