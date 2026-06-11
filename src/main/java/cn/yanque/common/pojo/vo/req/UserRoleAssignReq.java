package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户分配角色请求")
public class UserRoleAssignReq {

    @NotEmpty(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
