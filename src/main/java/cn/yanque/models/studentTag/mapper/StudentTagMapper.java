package cn.yanque.models.studentTag.mapper;

import cn.yanque.models.studentTag.pojo.entity.StudentTagEntity;
import cn.yanque.models.studentTag.pojo.vo.StudentTagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentTagMapper {

    void insert(StudentTagEntity entity);

    int updateByStudentId(StudentTagEntity entity);

    StudentTagEntity selectByStudentId(@Param("studentId") Long studentId);

    StudentTagEntity selectById(@Param("id") Long id);

    List<StudentTagVO> selectList(@Param("tagType") String tagType,
                                   @Param("keyword") String keyword);

    void confirm(@Param("id") Long id,
                 @Param("confirmedBy") Long confirmedBy,
                 @Param("tagType") String tagType);

    List<StudentTagVO> selectByTeacherId(@Param("teacherId") Long teacherId);
}
