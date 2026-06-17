package cn.yanque.models.homework.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.homework.mapper.HomeworkSubmissionMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkReviewReq;
import cn.yanque.models.homework.service.HomeworkSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业提交 Service 实现。
 */
@Service
public class HomeworkSubmissionServiceImpl implements HomeworkSubmissionService {

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Override
    public Long submit(HomeworkSubmissionEntity entity) {
        // 查询是否已提交过
        HomeworkSubmissionEntity existing = homeworkSubmissionMapper.selectByAssignmentAndStudent(
                entity.getAssignmentId(), entity.getStudentNo());

        if (existing != null) {
            // 已提交过，更新版本号并更新
            entity.setId(existing.getId());
            entity.setVersion(existing.getVersion() + 1);
            entity.setSubmitTime(LocalDateTime.now());
            homeworkSubmissionMapper.update(entity);
            return existing.getId();
        } else {
            // 首次提交
            entity.setVersion(1);
            entity.setSubmitTime(LocalDateTime.now());
            entity.setStatus("SUBMITTED");
            homeworkSubmissionMapper.insert(entity);
            return entity.getId();
        }
    }

    @Override
    public HomeworkSubmissionEntity getById(Long id) {
        return homeworkSubmissionMapper.selectById(id);
    }

    @Override
    public HomeworkSubmissionEntity getByAssignmentAndStudent(Long assignmentId, String studentNo) {
        return homeworkSubmissionMapper.selectByAssignmentAndStudent(assignmentId, studentNo);
    }

    @Override
    public List<HomeworkSubmissionEntity> listByAssignmentId(Long assignmentId) {
        return homeworkSubmissionMapper.selectByAssignmentId(assignmentId);
    }

    @Override
    public List<HomeworkSubmissionEntity> listByStudentNo(String studentNo) {
        return homeworkSubmissionMapper.selectByStudentNo(studentNo);
    }

    @Override
    public void review(Long id, HomeworkReviewReq req, Long reviewerId) {
        HomeworkSubmissionEntity entity = homeworkSubmissionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "提交记录不存在");
        }

        entity.setScore(req.getScore());
        entity.setTeacherComment(req.getTeacherComment());
        entity.setReviewTeacherId(reviewerId);
        entity.setReviewTime(LocalDateTime.now());
        entity.setStatus("REVIEWED");

        homeworkSubmissionMapper.update(entity);
    }
}
