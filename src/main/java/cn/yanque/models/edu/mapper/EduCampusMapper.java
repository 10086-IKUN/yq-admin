package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduCampusEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EduCampusMapper {

    void insert(EduCampusEntity entity);

    int updateById(EduCampusEntity entity);

    EduCampusEntity selectById(@Param("id") Long id);

    List<EduCampusEntity> selectPage(@Param("keyword") String keyword,
                                     @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
