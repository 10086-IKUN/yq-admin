package cn.yanque.models.homework.mapper;

import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeworkAssignmentMapper {
    int insert(HomeworkAssignmentEntity entity);

    int updateById(HomeworkAssignmentEntity entity);

    int deleteById(@Param("id") Long id);

    int closeById(@Param("id") Long id, @Param("teacherId") Long teacherId);

    int publishAnswer(HomeworkAssignmentEntity entity);

    int clearAttachment(@Param("id") Long id);

    int clearAnswerAttachment(@Param("id") Long id);

    HomeworkAssignmentEntity selectById(@Param("id") Long id);

    List<HomeworkAssignmentEntity> selectPage(@Param("keyword") String keyword,
                                              @Param("classId") Long classId,
                                              @Param("courseId") Long courseId,
                                              @Param("status") String status,
                                              @Param("answerPublishStatus") String answerPublishStatus);

    int countStudentsByClassId(@Param("classId") Long classId);
}
