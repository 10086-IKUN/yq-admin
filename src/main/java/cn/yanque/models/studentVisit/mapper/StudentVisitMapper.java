package cn.yanque.models.studentVisit.mapper;

import cn.yanque.models.studentVisit.pojo.entity.StudentVisitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StudentVisitMapper {

    void insert(StudentVisitEntity entity);

    void update(StudentVisitEntity entity);

    StudentVisitEntity selectById(@Param("id") Long id);

    /**
     * 查询某教师今日需回访的学员
     */
    List<StudentVisitEntity> selectTodayVisit(@Param("teacherId") Long teacherId,
                                               @Param("today") LocalDate today);

    /**
     * 查询某学员的回访历史
     */
    List<StudentVisitEntity> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询某学员最新的待回访记录
     */
    StudentVisitEntity selectLatestPending(@Param("studentId") Long studentId);
}
