package cn.yanque.models.users.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.PermissionCreateReq;
import cn.yanque.common.pojo.vo.req.PermissionPageReq;
import cn.yanque.common.pojo.vo.req.PermissionUpdateReq;
import cn.yanque.common.pojo.vo.res.PermissionCreateRes;
import cn.yanque.common.pojo.vo.res.PermissionDeleteRes;
import cn.yanque.common.pojo.vo.res.PermissionDetailRes;
import cn.yanque.common.pojo.vo.res.PermissionPageRes;
import cn.yanque.common.pojo.vo.res.PermissionUpdateRes;

public interface SysPermissionService {

    PermissionCreateRes addPermission(PermissionCreateReq req);

    PermissionUpdateRes updatePermission(PermissionUpdateReq req);

    PermissionDeleteRes deletePermission(Long id);

    PermissionDetailRes getPermissionById(Long id);

    PageResult<PermissionPageRes> pagePermission(PermissionPageReq req);
}
