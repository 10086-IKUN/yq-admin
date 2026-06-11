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

@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

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

    @Override
    public PermissionDetailRes getPermissionById(Long id) {
        SysPermissionEntity permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw BusinessException.PermissionNotExist;
        }
        return buildPermissionDetailRes(permission);
    }

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

    private PermissionDetailRes buildPermissionDetailRes(SysPermissionEntity permission) {
        PermissionDetailRes res = new PermissionDetailRes();
        BeanUtils.copyProperties(permission, res);
        return res;
    }

    private PermissionPageRes buildPermissionPageRes(SysPermissionEntity permission) {
        PermissionPageRes res = new PermissionPageRes();
        BeanUtils.copyProperties(permission, res);
        return res;
    }
}
