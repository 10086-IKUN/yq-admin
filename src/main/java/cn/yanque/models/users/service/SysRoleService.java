package cn.yanque.models.users.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.RolePermissionAssignReq;
import cn.yanque.common.pojo.vo.req.RoleCreateReq;
import cn.yanque.common.pojo.vo.req.RolePageReq;
import cn.yanque.common.pojo.vo.req.RoleUpdateReq;
import cn.yanque.common.pojo.vo.res.RoleCreateRes;
import cn.yanque.common.pojo.vo.res.RoleDeleteRes;
import cn.yanque.common.pojo.vo.res.RoleDetailRes;
import cn.yanque.common.pojo.vo.res.RolePageRes;
import cn.yanque.common.pojo.vo.res.RolePermissionAssignRes;
import cn.yanque.common.pojo.vo.res.RoleUpdateRes;

public interface SysRoleService {

    RoleCreateRes addRole(RoleCreateReq req);

    RoleUpdateRes updateRole(RoleUpdateReq req);

    RoleDeleteRes deleteRole(Long id);

    RoleDetailRes getRoleById(Long id);

    PageResult<RolePageRes> pageRole(RolePageReq req);

    RolePermissionAssignRes assignRolePermissions(Long roleId, RolePermissionAssignReq req);
}
