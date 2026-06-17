package cn.yanque.models.homework.mapper;

import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交 Mapper。
 */
@Mapper
public interface HomeworkSubmissionMapper {

    /**
     * 插入作业提交记录。
     * @param entity 提交实体
     * @return 影响行数
     */
    int insert(HomeworkSubmissionEntity entity);

    /**
     * 更新作业提交记录。
     * @param entity 提交实体
     * @return 影响行数
     */
    int update(HomeworkSubmissionEntity entity);

    /**
     * 根据 ID 查询提交记录。
     * @param id 提交ID
     * @return 提交实体
     */
    HomeworkSubmissionEntity selectById(@Param("id") Long id);

    /**
     * 根据作业ID和学员编号查询提交记录。
     * @param assignmentId 作业ID
     * @param studentNo 学员编号
     * @return 提交实体
     */
    HomeworkSubmissionEntity selectByAssignmentAndStudent(@Param("assignmentId") Long assignmentId, @Param("studentNo") String studentNo);

    /**
     * 根据作业ID查询所有提交记录。
     * @param assignmentId 作业ID
     * @return 提交列表
     */
    List<HomeworkSubmissionEntity> selectByAssignmentId(@Param("assignmentId") Long assignmentId);

    /**
     * 根据学员编号查询所有提交记录。
     * @param studentNo 学员编号
     * @return 提交列表
     */
    List<HomeworkSubmissionEntity> selectByStudentNo(@Param("studentNo") String studentNo);
}
