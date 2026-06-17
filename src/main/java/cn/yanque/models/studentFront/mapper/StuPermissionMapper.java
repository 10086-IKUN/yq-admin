package cn.yanque.models.studentFront.mapper;

import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StuPermissionMapper {

    List<StuPermissionEntity> selectByStudentId(@Param("studentId") Long studentId);
}
