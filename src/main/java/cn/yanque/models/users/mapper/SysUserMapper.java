package cn.yanque.models.users.mapper;

import cn.yanque.common.pojo.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    void insert(SysUserEntity user);

    int updateById(SysUserEntity user);

    SysUserEntity selectById(@Param("id") Long id);

    SysUserEntity selectByUsername(@Param("username") String username);

    List<SysUserEntity> selectPage(@Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("roleCode") String roleCode);

    int deleteById(@Param("id") Long id);

    int deleteUserRoles(@Param("userId") Long userId);

    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
