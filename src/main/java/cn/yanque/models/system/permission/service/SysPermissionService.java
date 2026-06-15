package cn.yanque.models.system.permission.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.system.permission.pojo.vo.req.PermissionCreateReq;
import cn.yanque.models.system.permission.pojo.vo.req.PermissionPageReq;
import cn.yanque.models.system.permission.pojo.vo.req.PermissionUpdateReq;
import cn.yanque.models.system.permission.pojo.vo.res.PermissionCreateRes;
import cn.yanque.models.system.permission.pojo.vo.res.PermissionDeleteRes;
import cn.yanque.models.system.permission.pojo.vo.res.PermissionDetailRes;
import cn.yanque.models.system.permission.pojo.vo.res.PermissionPageRes;
import cn.yanque.models.system.permission.pojo.vo.res.PermissionUpdateRes;

/**
 * 系统权限服务接口
 * 定义权限管理的业务逻辑方法
 */
public interface SysPermissionService {

    /**
     * 添加权限
     * @param req 创建权限请求参数
     * @return 创建成功的权限信息
     */
    PermissionCreateRes addPermission(PermissionCreateReq req);

    /**
     * 修改权限
     * @param req 更新权限请求参数
     * @return 更新后的权限信息
     */
    PermissionUpdateRes updatePermission(PermissionUpdateReq req);

    /**
     * 删除权限
     * @param id 权限ID
     * @return 删除结果
     */
    PermissionDeleteRes deletePermission(Long id);

    /**
     * 根据ID查询权限
     * @param id 权限ID
     * @return 权限详细信息
     */
    PermissionDetailRes getPermissionById(Long id);

    /**
     * 分页查询权限
     * @param req 分页查询参数
     * @return 分页权限列表
     */
    PageResult<PermissionPageRes> pagePermission(PermissionPageReq req);
}
