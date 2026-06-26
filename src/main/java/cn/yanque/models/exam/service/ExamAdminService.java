package cn.yanque.models.exam.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperEntity;
import cn.yanque.models.exam.pojo.entity.ExamQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionReq;
import cn.yanque.models.exam.pojo.vo.req.ExamReviewReq;
import cn.yanque.models.exam.pojo.vo.req.ExamSchedulePageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamScheduleReq;
import cn.yanque.models.exam.pojo.vo.res.ExamPaperDetailRes;

import java.util.List;

/**
 * 管理端考试服务。
 */
public interface ExamAdminService {

    /** 新增题目。 */
    Long createQuestion(ExamQuestionReq req, Long teacherId);

    /** 编辑题目。 */
    Long updateQuestion(Long id, ExamQuestionReq req);

    /** 删除未被试卷使用的题目。 */
    Long deleteQuestion(Long id);

    /** 分页查询题库。 */
    PageResult<ExamQuestionEntity> questionPage(ExamQuestionPageReq req);

    /** 新增试卷并保存题目配置。 */
    Long createPaper(ExamPaperReq req, Long teacherId);

    /** 编辑尚未被考试使用的试卷。 */
    Long updatePaper(Long id, ExamPaperReq req);

    /** 删除尚未发布考试的试卷。 */
    Long deletePaper(Long id);

    /** 查询试卷详情。 */
    ExamPaperDetailRes paperDetail(Long id);

    /** 分页查询试卷。 */
    PageResult<ExamPaperEntity> paperPage(ExamPaperPageReq req);

    /** 发布考试。 */
    Long createSchedule(ExamScheduleReq req, Long teacherId);

    /** 编辑尚无人参加的考试。 */
    Long updateSchedule(Long id, ExamScheduleReq req);

    /** 删除尚无人参加的考试。 */
    Long deleteSchedule(Long id);

    /** 老师提前发布答案和成绩。 */
    Long publishAnswers(Long id);

    /** 分页查询考试发布记录。 */
    PageResult<ExamScheduleEntity> schedulePage(ExamSchedulePageReq req);

    /** 查询某场考试的学员考试记录。 */
    List<ExamAttemptEntity> attempts(Long scheduleId);

    /** 查询某次考试记录的逐题答案。 */
    List<ExamAnswerEntity> attemptAnswers(Long attemptId);

    /** 批改一道主观题并刷新考试总分。 */
    Long reviewAnswer(Long answerId, ExamReviewReq req, Long teacherId);
}
