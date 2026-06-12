package cn.yanque.models.users.mapper;

import cn.yanque.common.pojo.entity.SysPermissionEntity;
import cn.yanque.common.pojo.vo.bo.QueryPermissionBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统权限Mapper接口
 * 定义权限相关的数据库操作，包括权限CRUD和角色权限关联操作
 */
public interface SysPermissionMapper {

    /**
     * 插入权限记录
     * @param permission 权限实体
     */
    void insert(SysPermissionEntity permission);

    /**
     * 根据ID更新权限记录
     * @param permission 权限实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(SysPermissionEntity permission);

    /**
     * 根据ID查询权限
     * @param id 权限ID
     * @return 权限实体
     */
    SysPermissionEntity selectById(@Param("id") Long id);

    /**
     * 根据权限编码查询权限
     * @param permissionCode 权限编码
     * @return 权限实体
     */
    SysPermissionEntity selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 分页查询权限（支持关键词、父级ID、权限类型、状态筛选）
     * @param keyword 搜索关键词
     * @param parentId 父级权限ID
     * @param permissionType 权限类型
     * @param status 权限状态
     * @return 权限列表
     */
    List<SysPermissionEntity> selectPage(@Param("keyword") String keyword,
                                         @Param("parentId") Long parentId,
                                         @Param("permissionType") String permissionType,
                                         @Param("status") String status);

    /**
     * 根据ID删除权限
     * @param id 权限ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 删除权限的所有角色关联
     * @param permissionId 权限ID
     * @return 受影响的行数
     */
    int deleteRolePermissions(@Param("permissionId") Long permissionId);

    /**
     * 根据条件查询权限列表
     * @param queryPermissionBo 查询条件
     * @return 权限列表
     */
    List<SysPermissionEntity> selectList(QueryPermissionBo queryPermissionBo);
}
