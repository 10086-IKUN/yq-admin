package cn.yanque.models.system.user.pojo.vo.res;

import cn.yanque.models.system.permission.pojo.vo.res.PermissionDetailRes;
import cn.yanque.models.system.role.pojo.vo.res.RoleDetailRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "登录响应")

/**
 * LoginRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class LoginRes {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "签名密钥")
    private String signSecret;

    @Schema(description = "用户详情")
    private UserDetailRes userDetailRes;

    @Schema(description = "角色列表")
    private List<RoleDetailRes> roleDetailResList;

    @Schema(description = "权限列表")
    private List<PermissionDetailRes> permissionDetailResList;
}
