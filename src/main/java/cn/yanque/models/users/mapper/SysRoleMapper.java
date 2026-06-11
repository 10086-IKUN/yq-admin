package cn.yanque.models.users.mapper;

import cn.yanque.common.pojo.entity.SysRoleEntity;
import cn.yanque.common.pojo.vo.bo.QueryUserBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {

    void insert(SysRoleEntity role);

    int updateById(SysRoleEntity role);

    SysRoleEntity selectById(@Param("id") Long id);

    SysRoleEntity selectByRoleCode(@Param("roleCode") String roleCode);

    List<SysRoleEntity> selectPage(@Param("keyword") String keyword, @Param("status") String status);

    int deleteById(@Param("id") Long id);

    int deleteRolePermissions(@Param("roleId") Long roleId);

    int deleteUserRoles(@Param("roleId") Long roleId);

    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    List<Long> selectPermissionIdsByRoleId(@Param("roleIds") List<Long> roleIds);

    List<SysRoleEntity> selectList(QueryUserBo queryUserBo);
}
