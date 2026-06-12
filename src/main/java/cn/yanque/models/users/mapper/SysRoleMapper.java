package cn.yanque.models.users.mapper;

import cn.yanque.common.pojo.entity.SysRoleEntity;
import cn.yanque.common.pojo.vo.bo.QueryUserBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色Mapper接口
 * 定义角色相关的数据库操作，包括角色CRUD和角色权限关联操作
 */
public interface SysRoleMapper {

    /**
     * 插入角色记录
     * @param role 角色实体
     */
    void insert(SysRoleEntity role);

    /**
     * 根据ID更新角色记录
     * @param role 角色实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(SysRoleEntity role);

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色实体
     */
    SysRoleEntity selectById(@Param("id") Long id);

    /**
     * 根据角色编码查询角色
     * @param roleCode 角色编码
     * @return 角色实体
     */
    SysRoleEntity selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 分页查询角色（支持关键词、状态筛选）
     * @param keyword 搜索关键词
     * @param status 角色状态
     * @return 角色列表
     */
    List<SysRoleEntity> selectPage(@Param("keyword") String keyword, @Param("status") String status);

    /**
     * 根据ID删除角色
     * @param id 角色ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 删除角色所有权限关联
     * @param roleId 角色ID
     * @return 受影响的行数
     */
    int deleteRolePermissions(@Param("roleId") Long roleId);

    /**
     * 删除角色所有用户关联
     * @param roleId 角色ID
     * @return 受影响的行数
     */
    int deleteUserRoles(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 受影响的行数
     */
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 查询角色的权限ID列表
     * @param roleIds 角色ID列表
     * @return 权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据条件查询角色列表
     * @param queryUserBo 查询条件
     * @return 角色列表
     */
    List<SysRoleEntity> selectList(QueryUserBo queryUserBo);
}
