package cn.yanque.models.homework.service;

import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkReviewReq;

import java.util.List;

/**
 * 作业提交 Service 接口。
 */
public interface HomeworkSubmissionService {

    /**
     * 提交作业（新增或更新）。
     * @param entity 提交实体
     * @return 提交ID
     */
    Long submit(HomeworkSubmissionEntity entity);

    /**
     * 根据 ID 查询提交记录。
     * @param id 提交ID
     * @return 提交实体
     */
    HomeworkSubmissionEntity getById(Long id);

    /**
     * 根据作业ID和学员编号查询提交记录。
     * @param assignmentId 作业ID
     * @param studentNo 学员编号
     * @return 提交实体
     */
    HomeworkSubmissionEntity getByAssignmentAndStudent(Long assignmentId, String studentNo);

    /**
     * 根据作业ID查询所有提交记录。
     * @param assignmentId 作业ID
     * @return 提交列表
     */
    List<HomeworkSubmissionEntity> listByAssignmentId(Long assignmentId);

    /**
     * 根据学员编号查询所有提交记录。
     * @param studentNo 学员编号
     * @return 提交列表
     */
    List<HomeworkSubmissionEntity> listByStudentNo(String studentNo);

    /**
     * 批改作业。
     * @param id 提交记录ID
     * @param req 批改请求（分数、评语）
     * @param reviewerId 批改老师ID
     */
    void review(Long id, HomeworkReviewReq req, Long reviewerId);
}
