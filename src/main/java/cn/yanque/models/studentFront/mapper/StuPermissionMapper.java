package cn.yanque.models.studentFront.mapper;

import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 学生端权限表访问接口。
 *
 * <p>根据学生当前有效权限模板，查询学生可访问的权限编码和接口路径。</p>
 */
public interface StuPermissionMapper {

    List<StuPermissionEntity> selectByStudentId(@Param("studentId") Long studentId);
}
