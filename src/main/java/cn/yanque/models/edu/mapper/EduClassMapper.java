package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 班级Mapper接口
 * 定义班级相关的数据库操作
 */
public interface EduClassMapper {

    /**
     * 插入班级记录
     * @param entity 班级实体
     */
    void insert(EduClassEntity entity);

    /**
     * 根据ID更新班级记录
     * @param entity 班级实体（包含要更新的字段）
     * @return 受影响的行数
     */
    int updateById(EduClassEntity entity);

    /**
     * 根据ID查询班级
     * @param id 班级ID
     * @return 班级实体
     */
    EduClassEntity selectById(@Param("id") Long id);

    /**
     * 分页查询班级（支持关键词、状态、校区ID筛选）
     * @param keyword 搜索关键词
     * @param classStatus 班级状态
     * @param campusId 校区ID
     * @return 班级列表
     */
    List<EduClassEntity> selectPage(@Param("keyword") String keyword,
                                    @Param("classStatus") String classStatus,
                                    @Param("campusId") Long campusId);

    /**
     * 根据ID删除班级
     * @param id 班级ID
     * @return 受影响的行数
     */
    int deleteById(@Param("id") Long id);
}
