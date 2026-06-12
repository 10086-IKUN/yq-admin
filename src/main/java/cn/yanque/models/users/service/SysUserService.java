package cn.yanque.models.users.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.info.UserInfo;
import cn.yanque.common.pojo.vo.req.*;
import cn.yanque.common.pojo.vo.res.*;

/**
 * 系统用户服务接口
 * 定义用户管理、登录认证、角色分配等业务逻辑方法
 */
public interface SysUserService {

    /**
     * 添加用户
     * @param req 创建用户请求参数
     * @return 创建成功的用户信息
     */
    UserCreateRes addUser(UserCreateReq req);

    /**
     * 修改用户
     * @param req 更新用户请求参数
     * @return 更新后的用户信息
     */
    UserUpdateRes updateUser(UserUpdateReq req);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    UserDeleteRes deleteUser(Long id);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户详细信息
     */
    UserDetailRes getUserById(Long id);

    /**
     * 分页查询用户
     * @param req 分页查询参数
     * @return 分页用户列表
     */
    PageResult<UserPageRes> pageUser(UserPageReq req);

    /**
     * 分配用户角色
     * @param userId 用户ID
     * @param req 角色分配请求参数
     * @return 角色分配结果
     */
    UserRoleAssignRes assignUserRoles(Long userId, UserRoleAssignReq req);

    /**
     * 用户登录
     * @param req 登录请求参数（用户名、密码）
     * @return 登录结果（包含Token）
     */
    LoginRes loginReq(LoginReq req);

    /**
     * 获取用户信息（含角色和权限）
     * @param userId 用户ID
     * @return 用户完整信息
     */
    UserInfo getUserInfo(Long userId);
}
