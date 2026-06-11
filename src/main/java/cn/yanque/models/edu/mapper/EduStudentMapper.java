package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduStudentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EduStudentMapper {

    void insert(EduStudentEntity entity);

    int updateById(EduStudentEntity entity);

    EduStudentEntity selectById(@Param("id") Long id);

    List<EduStudentEntity> selectPage(@Param("keyword") String keyword,
                                      @Param("studyMode") String studyMode,
                                      @Param("classId") Long classId,
                                      @Param("productId") Long productId);

    int deleteById(@Param("id") Long id);
}
