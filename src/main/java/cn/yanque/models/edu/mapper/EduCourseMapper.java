package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduCourseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EduCourseMapper {

    void insert(EduCourseEntity entity);

    int updateById(EduCourseEntity entity);

    EduCourseEntity selectById(@Param("id") Long id);

    List<EduCourseEntity> selectPage(@Param("keyword") String keyword,
                                     @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
