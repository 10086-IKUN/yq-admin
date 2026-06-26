package cn.yanque.models.homework.mapper;

import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业发布 Mapper。
 *
 * <p>这里操作的是 homework_assignment 作业发布表。
 * 注意：这张表里的记录本身就是老师已经发布给班级的作业，deleted=1 才表示逻辑删除。</p>
 */
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

    /**
     * 查询某个班级下所有未删除的作业。
     *
     * <p>学员标签的作业总数要统计“发布给该班级的全部作业”，
     * 不能只统计已截止或已关闭的作业，否则会和学员端首页看到的作业数量不一致。</p>
     *
     * @param classId 班级ID
     * @return 该班级下所有未删除作业
     */
    List<HomeworkAssignmentEntity> selectByClassId(@Param("classId") Long classId);

    /**
     * 查询某个班级下已经截止的作业。
     *
     * <p>这个方法保留给只需要统计历史截止作业的业务使用。</p>
     *
     * @param classId 班级ID
     * @return 已关闭或已过截止时间的作业
     */
    List<HomeworkAssignmentEntity> selectClosedByClassId(@Param("classId") Long classId);
}
