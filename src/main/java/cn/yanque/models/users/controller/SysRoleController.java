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

@RestController
@RequestMapping("/api/sysRole")
@Slf4j
@Tag(name = "SysRoleController", description = "系统角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @PostMapping
    @Operation(description = "添加角色")
    @RequirePermission("role:add")
    public ApiResponse<RoleCreateRes> addRole(@Valid @RequestBody RoleCreateReq req) {

        return ApiResponse.success(sysRoleService.addRole(req));
    }

    @PutMapping("{id}")
    @Operation(description = "修改角色")
    @RequirePermission("role:update")
    public ApiResponse<RoleUpdateRes> updateRole(@Parameter(description = "角色ID") @PathVariable Long id,
                                                 @Valid @RequestBody RoleUpdateReq req) {

        req.setId(id);
        return ApiResponse.success(sysRoleService.updateRole(req));
    }

    @DeleteMapping("{id}")
    @Operation(description = "删除角色")
    @RequirePermission("role:delete")
    public ApiResponse<RoleDeleteRes> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {

        return ApiResponse.success(sysRoleService.deleteRole(id));
    }

    @GetMapping("{id}")
    @Operation(description = "根据ID查询角色")
    @RequirePermission("role:view")
    public ApiResponse<RoleDetailRes> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {

        return ApiResponse.success(sysRoleService.getRoleById(id));
    }

    @GetMapping
    @Operation(description = "分页查询角色")
    @RequirePermission("role:view")
    public ApiResponse<PageResult<RolePageRes>> pageRole(@Valid @ModelAttribute RolePageReq req) {

        return ApiResponse.success(sysRoleService.pageRole(req));
    }

    @PutMapping("{id}/permissions")
    @Operation(description = "角色分配权限")
    @RequirePermission("role:assign")
    public ApiResponse<RolePermissionAssignRes> assignRolePermissions(@Parameter(description = "角色ID") @PathVariable Long id,
                                                                      @Valid @RequestBody RolePermissionAssignReq req) {

        return ApiResponse.success(sysRoleService.assignRolePermissions(id, req));
    }
}
