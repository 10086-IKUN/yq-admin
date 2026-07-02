package cn.yanque.models.studentTag.mapper;

import cn.yanque.models.studentTag.pojo.entity.StudentTagEntity;
import cn.yanque.models.studentTag.pojo.vo.StudentTagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

/**
 * 学生标签表访问接口。
 *
 * <p>负责标签的写入、确认、列表查询，以及按班主任查询其班级学生标签。</p>
 */
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
