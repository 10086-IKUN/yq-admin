package cn.yanque.models.users.mapper;

import cn.yanque.common.pojo.entity.SysPermissionEntity;
import cn.yanque.common.pojo.vo.bo.QueryPermissionBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper {

    void insert(SysPermissionEntity permission);

    int updateById(SysPermissionEntity permission);

    SysPermissionEntity selectById(@Param("id") Long id);

    SysPermissionEntity selectByPermissionCode(@Param("permissionCode") String permissionCode);

    List<SysPermissionEntity> selectPage(@Param("keyword") String keyword,
                                         @Param("parentId") Long parentId,
                                         @Param("permissionType") String permissionType,
                                         @Param("status") String status);

    int deleteById(@Param("id") Long id);

    int deleteRolePermissions(@Param("permissionId") Long permissionId);

    List<SysPermissionEntity> selectList(QueryPermissionBo queryPermissionBo);
}
