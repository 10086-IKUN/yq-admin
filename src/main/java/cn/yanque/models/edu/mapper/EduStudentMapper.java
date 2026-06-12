package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduStudentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper接口
 * 定义学生相关的数据库操作
 */
public interface EduStudentMapper {

    /**
     * 插入学生记录
     * @param entity 学生实体
     */
    void insert(EduStudentEntity entity);

    /**
     * 根据ID更新学生记录
     * @param entity 学生实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(EduStudentEntity entity);

    /**
     * 根据ID查询学生
     * @param id 学生ID
     * @return 学生实体
     */
    EduStudentEntity selectById(@Param("id") Long id);

    /**
     * 分页查询学生（支持关键词、学习模式、班级ID、产品ID筛选）
     * @param keyword 搜索关键词
     * @param studyMode 学习模式
     * @param classId 班级ID
     * @param productId 产品ID
     * @return 学生列表
     */
    List<EduStudentEntity> selectPage(@Param("keyword") String keyword,
                                      @Param("studyMode") String studyMode,
                                      @Param("classId") Long classId,
                                      @Param("productId") Long productId);

    /**
     * 根据ID删除学生
     * @param id 学生ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);
}
