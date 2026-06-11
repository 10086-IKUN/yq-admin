package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface EduClassMapper {

    void insert(EduClassEntity entity);

    int updateById(EduClassEntity entity);

    EduClassEntity selectById(@Param("id") Long id);

    List<EduClassEntity> selectPage(@Param("keyword") String keyword,
                                    @Param("classStatus") String classStatus,
                                    @Param("campusId") Long campusId);

    int deleteById(@Param("id") Long id);
}
