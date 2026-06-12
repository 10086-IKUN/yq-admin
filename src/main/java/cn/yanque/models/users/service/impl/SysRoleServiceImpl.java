package cn.yanque.models.users.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.users.mapper.SysRoleMapper;
import cn.yanque.common.pojo.entity.SysRoleEntity;
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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 系统角色服务实现类
 * 实现角色管理、权限分配等业务逻辑
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 添加角色（同时分配权限）
     * @param req 创建角色请求参数
     * @return 创建成功的角色ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleCreateRes addRole(RoleCreateReq req) {
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleCode(req.getRoleCode());
        role.setRoleName(req.getRoleName());
        role.setDescription(req.getDescription());
        role.setStatus(req.getStatus());
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());

        try {
            sysRoleMapper.insert(role);
            resetRolePermissions(role.getId(), req.getPermissionIds());
        } catch (DuplicateKeyException e) {
            throw BusinessException.RoleExist;
        }

        RoleCreateRes res = new RoleCreateRes();
        res.setId(role.getId());
        return res;
    }

    /**
     * 修改角色（同时更新权限）
     * @param req 更新角色请求参数
     * @return 更新后的角色ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleUpdateRes updateRole(RoleUpdateReq req) {
        SysRoleEntity role = new SysRoleEntity();
        role.setId(req.getId());
        role.setRoleCode(req.getRoleCode());
        role.setRoleName(req.getRoleName());
        role.setDescription(req.getDescription());
        role.setStatus(req.getStatus());
        role.setUpdatedAt(new Date());

        int rows;
        try {
            rows = sysRoleMapper.updateById(role);
            if (rows == 0) {
                throw BusinessException.RoleNotExist;
            }
            resetRolePermissions(req.getId(), req.getPermissionIds());
        } catch (DuplicateKeyException e) {
            throw BusinessException.RoleExist;
        }

        RoleUpdateRes res = new RoleUpdateRes();
        res.setId(req.getId());
        return res;
    }

    /**
     * 删除角色（同时删除角色权限和用户角色关联）
     * @param id 角色ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDeleteRes deleteRole(Long id) {
        sysRoleMapper.deleteRolePermissions(id);
        sysRoleMapper.deleteUserRoles(id);
        int rows = sysRoleMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.RoleNotExist;
        }

        RoleDeleteRes res = new RoleDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询角色详情（含权限ID列表）
     * @param id 角色ID
     * @return 角色详细信息
     */
    @Override
    public RoleDetailRes getRoleById(Long id) {
        SysRoleEntity role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw BusinessException.RoleNotExist;
        }
        RoleDetailRes res = buildRoleDetailRes(role);
        res.setPermissionIds(sysRoleMapper.selectPermissionIdsByRoleId(Collections.singletonList(id)));
        return res;
    }

    /**
     * 分页查询角色
     * @param req 分页查询参数（关键词、状态）
     * @return 分页角色列表
     */
    @Override
    public PageResult<RolePageRes> pageRole(RolePageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<SysRoleEntity> list = sysRoleMapper.selectPage(req.getKeyword(), req.getStatus());
        PageInfo<SysRoleEntity> pageInfo = new PageInfo<>(list);
        List<RolePageRes> records = list.stream().map(this::buildRolePageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    /**
     * 分配角色权限（全量替换）
     * @param roleId 角色ID
     * @param req 权限分配请求参数
     * @return 权限分配结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RolePermissionAssignRes assignRolePermissions(Long roleId, RolePermissionAssignReq req) {
        SysRoleEntity role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw BusinessException.RoleNotExist;
        }

        this.resetRolePermissions(roleId, req.getPermissionIds());

        RolePermissionAssignRes res = new RolePermissionAssignRes();
        res.setRoleId(roleId);
        res.setPermissionIds(req.getPermissionIds());
        return res;
    }

    /**
     * 重置角色权限（先删后插）
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    public void resetRolePermissions(Long roleId, List<Long> permissionIds) {
        sysRoleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            sysRoleMapper.insertRolePermissions(roleId, permissionIds);
        }
    }

    /**
     * 构建角色详情响应对象
     * @param role 角色实体
     * @return 角色详情
     */
    private RoleDetailRes buildRoleDetailRes(SysRoleEntity role) {
        RoleDetailRes res = new RoleDetailRes();
        BeanUtils.copyProperties(role, res);
        return res;
    }

    /**
     * 构建角色分页响应对象
     * @param role 角色实体
     * @return 角色分页信息
     */
    private RolePageRes buildRolePageRes(SysRoleEntity role) {
        RolePageRes res = new RolePageRes();
        BeanUtils.copyProperties(role, res);
        return res;
    }
}
