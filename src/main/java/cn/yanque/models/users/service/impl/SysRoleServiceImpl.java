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

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

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

    public void resetRolePermissions(Long roleId, List<Long> permissionIds) {
        sysRoleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            sysRoleMapper.insertRolePermissions(roleId, permissionIds);
        }
    }

    private RoleDetailRes buildRoleDetailRes(SysRoleEntity role) {
        RoleDetailRes res = new RoleDetailRes();
        BeanUtils.copyProperties(role, res);
        return res;
    }

    private RolePageRes buildRolePageRes(SysRoleEntity role) {
        RolePageRes res = new RolePageRes();
        BeanUtils.copyProperties(role, res);
        return res;
    }
}
