package cn.yanque.models.exam.service;

import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamAnswerSaveReq;
import cn.yanque.models.exam.pojo.vo.res.StudentExamDetailRes;

import java.util.List;

/**
 * 学员端在线考试服务。
 */
public interface StudentExamService {

    /** 查询当前学员所在班级的考试。 */
    List<ExamScheduleEntity> list(Long studentId);

    /** 查询考试详情和当前学员已有的考试记录。 */
    StudentExamDetailRes detail(Long scheduleId, Long studentId);

    /** 首次进入考试并创建唯一考试记录。 */
    StudentExamDetailRes start(Long scheduleId, Long studentId);

    /** 在交卷前批量保存答案。 */
    void saveAnswers(Long attemptId, ExamAnswerSaveReq req, Long studentId);

    /** 最终交卷并自动批改客观题。 */
    StudentExamDetailRes submit(Long attemptId, Long studentId);
}
