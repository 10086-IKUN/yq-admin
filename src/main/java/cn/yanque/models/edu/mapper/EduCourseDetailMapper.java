package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduCourseDetailEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程详情Mapper接口
 *
 * 设计思路：
 * 1. Mapper接口定义数据库操作方法，具体SQL写在XML文件中
 * 2. MyBatis会自动生成实现类，通过@MapperScan扫描并注册到Spring容器
 * 3. @Param注解用于在XML中引用方法参数
 *
 * 方法命名规范：
 * - insert: 插入新记录
 * - updateById: 根据ID更新记录
 * - selectById: 根据ID查询单条记录
 * - selectByCourseId: 根据课程ID查询多条记录
 * - deleteById: 根据ID删除记录
 * - deleteByCourseId: 根据课程ID删除所有相关记录（级联删除）
 */
public interface EduCourseDetailMapper {

    /** 插入新课程详情，插入后实体的id字段会自动填充（useGeneratedKeys） */
    void insert(EduCourseDetailEntity entity);

    /** 根据ID更新课程详情，返回受影响的行数（0表示记录不存在） */
    int updateById(EduCourseDetailEntity entity);

    /** 根据ID查询单条课程详情 */
    EduCourseDetailEntity selectById(@Param("id") Long id);

    /** 根据课程ID查询该课程的所有详情，按阶段和天数排序 */
    List<EduCourseDetailEntity> selectByCourseId(@Param("courseId") Long courseId);

    /** 根据ID删除单条课程详情 */
    int deleteById(@Param("id") Long id);

    /** 根据课程ID删除该课程的所有详情（用于删除课程时级联删除） */
    int deleteByCourseId(@Param("courseId") Long courseId);
    /** 批量插入*/
    void insertBatch(List<EduCourseDetailEntity> entityList);

    /**
     * 检查课程中是否已存在指定天数的记录
     * @param courseId 课程ID
     * @param dayNum 天数
     * @param excludeId 排除的记录ID（更新时排除自身）
     * @return 符合条件的记录数量
     */
    int countByCourseIdAndDayNum(@Param("courseId") Long courseId,
                                  @Param("dayNum") Integer dayNum,
                                  @Param("excludeId") Long excludeId);

    /** 查询课程的所有不重复阶段名称 */
    List<String> selectDistinctStageNames(@Param("courseId") Long courseId);
}
