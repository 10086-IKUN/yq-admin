package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkSubmissionReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.homework.service.HomeworkSubmissionService;
import cn.yanque.models.studentFront.biz.StudentHomeworkBiz;
import cn.yanque.models.studentFront.service.StudentHomeworkService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 学员端作业业务实现类
 * 实现学员端作业相关的业务逻辑
 */
@Component
public class StudentHomeworkBizImpl implements StudentHomeworkBiz {

    @Autowired
    private StudentHomeworkService studentHomeworkService;

    @Autowired
    private HomeworkSubmissionService homeworkSubmissionService;

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    @Override
    public PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req, String studentNo) {
        return studentHomeworkService.list(req, studentNo);
    }

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业详情
     */
    @Override
    public HomeworkAssignmentRes detail(Long id) {
        HomeworkAssignmentEntity entity = studentHomeworkService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "作业不存在");
        }
        return convertToRes(entity);
    }

    /**
     * 提交作业
     * @param req 提交请求
     * @param studentNo 学员编号
     * @param studentName 学员姓名
     * @return 提交记录
     */
    @Override
    public HomeworkSubmissionEntity submit(HomeworkSubmissionReq req, String studentNo, String studentName) {
        // 验证作业是否存在
        HomeworkAssignmentEntity assignment = studentHomeworkService.getById(req.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        // 构建提交实体
        HomeworkSubmissionEntity entity = new HomeworkSubmissionEntity();
        entity.setAssignmentId(req.getAssignmentId());
        entity.setStudentNo(studentNo);
        entity.setStudentNameSnapshot(studentName);
        entity.setSubmitContent(req.getSubmitContent());
        entity.setAttachmentName(req.getAttachmentName());
        entity.setAttachmentUrl(req.getAttachmentUrl());
        entity.setAttachmentType(req.getAttachmentType());

        homeworkSubmissionService.submit(entity);
        return entity;
    }

    /**
     * 获取学员的作业提交记录
     * @param assignmentId 作业ID
     * @param studentNo 学员编号
     * @return 提交记录
     */
    @Override
    public HomeworkSubmissionEntity getSubmission(Long assignmentId, String studentNo) {
        return homeworkSubmissionService.getByAssignmentAndStudent(assignmentId, studentNo);
    }

    /**
     * 实体转响应对象
     * @param entity 作业实体
     * @return 作业响应对象
     */
    private HomeworkAssignmentRes convertToRes(HomeworkAssignmentEntity entity) {
        HomeworkAssignmentRes res = new HomeworkAssignmentRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
