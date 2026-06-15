package cn.yanque.models.system.user.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.system.user.pojo.vo.req.*;
import cn.yanque.models.system.user.pojo.vo.res.*;
import cn.yanque.models.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户管理控制器
 * 提供用户的增删改查、角色分配、登录等接口
 */
@RestController
@RequestMapping("/api/sysUser")
@Slf4j
@Tag(name = "SysUserController", description = "系统用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 添加用户
     * @param req 创建用户请求参数
     * @return 创建成功的用户信息
     */
    @PostMapping
    @Operation(description = "添加用户")
    @RequirePermission("user:create")
    public ApiResponse<UserCreateRes> addUser(@Valid @RequestBody UserCreateReq req){

        return ApiResponse.success(sysUserService.addUser(req));
    }


    /**
     * 修改用户
     * @param id 用户ID
     * @param req 更新用户请求参数
     * @return 更新后的用户信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改用户")
    @RequirePermission("user:update")
    public ApiResponse<UserUpdateRes> updateUser(@Parameter(description = "用户ID") @PathVariable Long id,
                                                 @Valid @RequestBody UserUpdateReq req){

        req.setId(id);
        return ApiResponse.success(sysUserService.updateUser(req));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除用户")
    @RequirePermission("user:delete")
    public ApiResponse<UserDeleteRes> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id){

        return ApiResponse.success(sysUserService.deleteUser(id));
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询用户")
    @RequirePermission("user:view")
    public ApiResponse<UserDetailRes> getUserById(@Parameter(description = "用户ID") @PathVariable Long id){

        return ApiResponse.success(sysUserService.getUserById(id));
    }

    /**
     * 分页查询用户
     * @param req 分页查询参数
     * @return 分页用户列表
     */
    @GetMapping
    @Operation(description = "分页查询用户")
    @RequirePermission("user:view")
    public ApiResponse<PageResult<UserPageRes>> pageUser(@Valid @ModelAttribute UserPageReq req){

        return ApiResponse.success(sysUserService.pageUser(req));
    }

    /**
     * 用户分配角色
     * @param id 用户ID
     * @param req 角色分配请求参数
     * @return 分配结果
     */
    @PutMapping("{id}/roles")
    @Operation(description = "用户分配角色")
    @RequirePermission("user:assign-role")
    public ApiResponse<UserRoleAssignRes> assignUserRoles(@Parameter(description = "用户ID") @PathVariable Long id,
                                                          @Valid @RequestBody UserRoleAssignReq req){

        return ApiResponse.success(sysUserService.assignUserRoles(id, req));
    }

    /**
     * 用户登录
     * @param req 登录请求参数（用户名、密码）
     * @return 登录结果（包含token）
     */
    @PostMapping("/login")
    @Operation(description = "用户登录")
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq req){
        return ApiResponse.success(sysUserService.loginReq(req));
    }

}
