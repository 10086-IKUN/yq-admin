package cn.yanque.models.system.user.mapper;

import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户Mapper接口
 * 定义用户相关的数据库操作，包括用户CRUD和用户角色关联操作
 */
public interface SysUserMapper {

    /**
     * 插入用户记录
     * @param user 用户实体
     */
    void insert(SysUserEntity user);

    /**
     * 根据ID更新用户记录
     * @param user 用户实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(SysUserEntity user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    SysUserEntity selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    SysUserEntity selectByUsername(@Param("username") String username);

    /**
     * 分页查询用户（支持关键词、状态、角色编码筛选）
     * @param keyword 搜索关键词
     * @param status 用户状态
     * @param roleCode 角色编码
     * @return 用户列表
     */
    List<SysUserEntity> selectPage(@Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("roleCode") String roleCode);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 删除用户所有角色关联
     * @param userId 用户ID
     * @return 受影响的行数
     */
    int deleteUserRoles(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 受影响的行数
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 查询用户的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据多个ID批量查询用户（解决N+1查询问题）
     * @param ids 用户ID集合
     * @return 用户列表
     */
    List<SysUserEntity> selectByIds(@Param("ids") java.util.Set<Long> ids);
}
