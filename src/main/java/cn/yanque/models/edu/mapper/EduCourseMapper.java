package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduCourseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程Mapper接口
 * 定义课程相关的数据库操作
 */
public interface EduCourseMapper {

    /**
     * 插入课程记录
     * @param entity 课程实体
     */
    void insert(EduCourseEntity entity);

    /**
     * 根据ID更新课程记录
     * @param entity 课程实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(EduCourseEntity entity);

    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程实体
     */
    EduCourseEntity selectById(@Param("id") Long id);

    /**
     * 分页查询课程
     * @param keyword 搜索关键词（课程名称）
     * @param status 课程状态
     * @return 课程列表
     */
    List<EduCourseEntity> selectPage(@Param("keyword") String keyword,
                                     @Param("status") Integer status);

    /**
     * 根据ID删除课程
     * @param id 课程ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据多个ID批量查询课程（解决N+1查询问题）
     * @param ids 课程ID集合
     * @return 课程列表
     */
    List<EduCourseEntity> selectByIds(@Param("ids") java.util.Set<Long> ids);
}
