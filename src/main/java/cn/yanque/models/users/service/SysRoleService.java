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

/**
 * 系统角色服务接口
 * 定义角色管理、权限分配等业务逻辑方法
 */
public interface SysRoleService {

    /**
     * 添加角色
     * @param req 创建角色请求参数
     * @return 创建成功的角色信息
     */
    RoleCreateRes addRole(RoleCreateReq req);

    /**
     * 修改角色
     * @param req 更新角色请求参数
     * @return 更新后的角色信息
     */
    RoleUpdateRes updateRole(RoleUpdateReq req);

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    RoleDeleteRes deleteRole(Long id);

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色详细信息
     */
    RoleDetailRes getRoleById(Long id);

    /**
     * 分页查询角色
     * @param req 分页查询参数
     * @return 分页角色列表
     */
    PageResult<RolePageRes> pageRole(RolePageReq req);

    /**
     * 分配角色权限
     * @param roleId 角色ID
     * @param req 权限分配请求参数
     * @return 权限分配结果
     */
    RolePermissionAssignRes assignRolePermissions(Long roleId, RolePermissionAssignReq req);
}
