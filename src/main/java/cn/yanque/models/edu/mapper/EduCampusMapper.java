package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduCampusEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 校区Mapper接口
 * 定义校区相关的数据库操作
 */
public interface EduCampusMapper {

    /**
     * 插入校区记录
     * @param entity 校区实体
     */
    void insert(EduCampusEntity entity);

    /**
     * 根据ID更新校区记录
     * @param entity 校区实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(EduCampusEntity entity);

    /**
     * 根据ID查询校区
     * @param id 校区ID
     * @return 校区实体
     */
    EduCampusEntity selectById(@Param("id") Long id);

    /**
     * 分页查询校区
     * @param keyword 搜索关键词（校区名称）
     * @param status 校区状态
     * @return 校区列表
     */
    List<EduCampusEntity> selectPage(@Param("keyword") String keyword,
                                     @Param("status") Integer status);

    /**
     * 根据ID删除校区
     * @param id 校区ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据多个ID批量查询校区（解决N+1查询问题）
     * @param ids 校区ID集合
     * @return 校区列表
     */
    List<EduCampusEntity> selectByIds(@Param("ids") java.util.Set<Long> ids);
}
