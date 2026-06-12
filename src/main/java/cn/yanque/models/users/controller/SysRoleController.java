package cn.yanque.models.users.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.RoleCreateReq;
import cn.yanque.common.pojo.vo.req.RolePageReq;
import cn.yanque.common.pojo.vo.req.RolePermissionAssignReq;
import cn.yanque.common.pojo.vo.req.RoleUpdateReq;
import cn.yanque.common.pojo.vo.res.RoleCreateRes;
import cn.yanque.common.pojo.vo.res.RoleDeleteRes;
import cn.yanque.common.pojo.vo.res.RoleDetailRes;
import cn.yanque.common.pojo.vo.res.RolePageRes;
import cn.yanque.common.pojo.vo.res.RolePermissionAssignRes;
import cn.yanque.common.pojo.vo.res.RoleUpdateRes;
import cn.yanque.models.users.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * 系统角色管理控制器
 * 提供角色的增删改查、权限分配等接口
 */
@RestController
@RequestMapping("/api/sysRole")
@Slf4j
@Tag(name = "SysRoleController", description = "系统角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 添加角色
     * @param req 创建角色请求参数
     * @return 创建成功的角色信息
     */
    @PostMapping
    @Operation(description = "添加角色")
    @RequirePermission("role:add")
    public ApiResponse<RoleCreateRes> addRole(@Valid @RequestBody RoleCreateReq req) {

        return ApiResponse.success(sysRoleService.addRole(req));
    }

    /**
     * 修改角色
     * @param id 角色ID
     * @param req 更新角色请求参数
     * @return 更新后的角色信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改角色")
    @RequirePermission("role:update")
    public ApiResponse<RoleUpdateRes> updateRole(@Parameter(description = "角色ID") @PathVariable Long id,
                                                 @Valid @RequestBody RoleUpdateReq req) {

        req.setId(id);
        return ApiResponse.success(sysRoleService.updateRole(req));
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除角色")
    @RequirePermission("role:delete")
    public ApiResponse<RoleDeleteRes> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {

        return ApiResponse.success(sysRoleService.deleteRole(id));
    }

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询角色")
    @RequirePermission("role:view")
    public ApiResponse<RoleDetailRes> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {

        return ApiResponse.success(sysRoleService.getRoleById(id));
    }

    /**
     * 分页查询角色
     * @param req 分页查询参数
     * @return 分页角色列表
     */
    @GetMapping
    @Operation(description = "分页查询角色")
    @RequirePermission("role:view")
    public ApiResponse<PageResult<RolePageRes>> pageRole(@Valid @ModelAttribute RolePageReq req) {

        return ApiResponse.success(sysRoleService.pageRole(req));
    }

    /**
     * 角色分配权限
     * @param id 角色ID
     * @param req 权限分配请求参数
     * @return 分配结果
     */
    @PutMapping("{id}/permissions")
    @Operation(description = "角色分配权限")
    @RequirePermission("role:assign")
    public ApiResponse<RolePermissionAssignRes> assignRolePermissions(@Parameter(description = "角色ID") @PathVariable Long id,
                                                                      @Valid @RequestBody RolePermissionAssignReq req) {

        return ApiResponse.success(sysRoleService.assignRolePermissions(id, req));
    }
}
