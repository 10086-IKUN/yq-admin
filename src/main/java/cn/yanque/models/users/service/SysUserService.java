package cn.yanque.models.users.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.info.UserInfo;
import cn.yanque.common.pojo.vo.req.*;
import cn.yanque.common.pojo.vo.res.*;

public interface SysUserService {
    UserCreateRes addUser(UserCreateReq req);

    UserUpdateRes updateUser(UserUpdateReq req);

    UserDeleteRes deleteUser(Long id);

    UserDetailRes getUserById(Long id);

    PageResult<UserPageRes> pageUser(UserPageReq req);

    UserRoleAssignRes assignUserRoles(Long userId, UserRoleAssignReq req);

    LoginRes LoginReq(LoginReq req);

    UserInfo getUserInfo(Long userId);
}
