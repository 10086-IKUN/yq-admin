package cn.yanque.models.users.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.users.mapper.SysPermissionMapper;
import cn.yanque.common.pojo.entity.SysPermissionEntity;
import cn.yanque.common.pojo.vo.req.PermissionCreateReq;
import cn.yanque.common.pojo.vo.req.PermissionPageReq;
import cn.yanque.common.pojo.vo.req.PermissionUpdateReq;
import cn.yanque.common.pojo.vo.res.PermissionCreateRes;
import cn.yanque.common.pojo.vo.res.PermissionDeleteRes;
import cn.yanque.common.pojo.vo.res.PermissionDetailRes;
import cn.yanque.common.pojo.vo.res.PermissionPageRes;
import cn.yanque.common.pojo.vo.res.PermissionUpdateRes;
import cn.yanque.models.users.service.SysPermissionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 系统权限服务实现类
 * 实现权限管理的业务逻辑，包括增删改查
 */
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 添加权限
     * @param req 创建权限请求参数
     * @return 创建成功的权限ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionCreateRes addPermission(PermissionCreateReq req) {
        SysPermissionEntity permission = new SysPermissionEntity();
        permission.setParentId(req.getParentId());
        permission.setPermissionCode(req.getPermissionCode());
        permission.setPermissionName(req.getPermissionName());
        permission.setPermissionType(req.getPermissionType());
        permission.setApiPath(req.getApiPath());
        permission.setSortNum(req.getSortNum());
        permission.setDescription(req.getDescription());
        permission.setStatus(req.getStatus());
        permission.setCreatedAt(new Date());
        permission.setUpdatedAt(new Date());

        try {
            sysPermissionMapper.insert(permission);
        } catch (DuplicateKeyException e) {
            throw BusinessException.PermissionExist;
        }

        PermissionCreateRes res = new PermissionCreateRes();
        res.setId(permission.getId());
        return res;
    }

    /**
     * 修改权限
     * @param req 更新权限请求参数
     * @return 更新后的权限ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionUpdateRes updatePermission(PermissionUpdateReq req) {
        SysPermissionEntity permission = new SysPermissionEntity();
        permission.setId(req.getId());
        permission.setParentId(req.getParentId());
        permission.setPermissionCode(req.getPermissionCode());
        permission.setPermissionName(req.getPermissionName());
        permission.setPermissionType(req.getPermissionType());
        permission.setApiPath(req.getApiPath());
        permission.setSortNum(req.getSortNum());
        permission.setDescription(req.getDescription());
        permission.setStatus(req.getStatus());
        permission.setUpdatedAt(new Date());

        int rows;
        try {
            rows = sysPermissionMapper.updateById(permission);
        } catch (DuplicateKeyException e) {
            throw BusinessException.PermissionExist;
        }
        if (rows == 0) {
            throw BusinessException.PermissionNotExist;
        }

        PermissionUpdateRes res = new PermissionUpdateRes();
        res.setId(req.getId());
        return res;
    }

    /**
     * 删除权限（同时删除角色权限关联）
     * @param id 权限ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionDeleteRes deletePermission(Long id) {
        sysPermissionMapper.deleteRolePermissions(id);
        int rows = sysPermissionMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.PermissionNotExist;
        }

        PermissionDeleteRes res = new PermissionDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询权限详情
     * @param id 权限ID
     * @return 权限详细信息
     */
    @Override
    public PermissionDetailRes getPermissionById(Long id) {
        SysPermissionEntity permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw BusinessException.PermissionNotExist;
        }
        return buildPermissionDetailRes(permission);
    }

    /**
     * 分页查询权限
     * @param req 分页查询参数（关键词、父级ID、权限类型、状态）
     * @return 分页权限列表
     */
    @Override
    public PageResult<PermissionPageRes> pagePermission(PermissionPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<SysPermissionEntity> list = sysPermissionMapper.selectPage(
                req.getKeyword(),
                req.getParentId(),
                req.getPermissionType(),
                req.getStatus()
        );
        PageInfo<SysPermissionEntity> pageInfo = new PageInfo<>(list);
        List<PermissionPageRes> records = list.stream().map(this::buildPermissionPageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    /**
     * 构建权限详情响应对象
     * @param permission 权限实体
     * @return 权限详情
     */
    private PermissionDetailRes buildPermissionDetailRes(SysPermissionEntity permission) {
        PermissionDetailRes res = new PermissionDetailRes();
        BeanUtils.copyProperties(permission, res);
        return res;
    }

    /**
     * 构建权限分页响应对象
     * @param permission 权限实体
     * @return 权限分页信息
     */
    private PermissionPageRes buildPermissionPageRes(SysPermissionEntity permission) {
        PermissionPageRes res = new PermissionPageRes();
        BeanUtils.copyProperties(permission, res);
        return res;
    }
}
