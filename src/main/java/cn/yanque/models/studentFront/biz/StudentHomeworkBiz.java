package cn.yanque.models.studentFront.biz;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkSubmissionReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;

/**
 * 学员端作业业务接口
 * 定义学员端作业相关的业务逻辑方法
 */
public interface StudentHomeworkBiz {

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req, String studentNo);

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业详情
     */
    HomeworkAssignmentRes detail(Long id);

    /**
     * 提交作业
     * @param req 提交请求
     * @param studentNo 学员编号
     * @param studentName 学员姓名
     * @return 提交记录
     */
    HomeworkSubmissionEntity submit(HomeworkSubmissionReq req, String studentNo, String studentName);

    /**
     * 获取学员的作业提交记录
     * @param assignmentId 作业ID
     * @param studentNo 学员编号
     * @return 提交记录
     */
    HomeworkSubmissionEntity getSubmission(Long assignmentId, String studentNo);
}
