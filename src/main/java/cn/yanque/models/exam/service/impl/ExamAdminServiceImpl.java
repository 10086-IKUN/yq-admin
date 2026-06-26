package cn.yanque.models.exam.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.exam.mapper.ExamMapper;
import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperQuestionReq;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionReq;
import cn.yanque.models.exam.pojo.vo.req.ExamReviewReq;
import cn.yanque.models.exam.pojo.vo.req.ExamSchedulePageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamScheduleReq;
import cn.yanque.models.exam.pojo.vo.res.ExamPaperDetailRes;
import cn.yanque.models.exam.service.ExamAdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理端考试服务实现。
 *
 * <p>本类负责题库、试卷、考试发布和主观题批改。
 * 涉及多张表的保存操作全部放在事务中，避免只保存了一半数据。</p>
 */
@Service
public class ExamAdminServiceImpl implements ExamAdminService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public Long createQuestion(ExamQuestionReq req, Long teacherId) {
        validateQuestion(req);
        ExamQuestionEntity entity = new ExamQuestionEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setCreatedBy(teacherId);
        examMapper.insertQuestion(entity);
        return entity.getId();
    }

    @Override
    public Long updateQuestion(Long id, ExamQuestionReq req) {
        ensureQuestion(id);
        validateQuestion(req);
        ExamQuestionEntity entity = new ExamQuestionEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setId(id);
        examMapper.updateQuestion(entity);
        return id;
    }

    @Override
    public Long deleteQuestion(Long id) {
        ensureQuestion(id);
        if (examMapper.deleteQuestion(id) == 0) {
            throw new BusinessException(400, "题目已被试卷使用，不能删除");
        }
        return id;
    }

    @Override
    public PageResult<ExamQuestionEntity> questionPage(ExamQuestionPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<ExamQuestionEntity> list = examMapper.selectQuestionPage(
                req.getKeyword(), req.getQuestionType(), req.getStatus());
        PageInfo<ExamQuestionEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(ExamPaperReq req, Long teacherId) {
        BigDecimal totalScore = validateAndCalculatePaper(req);
        ExamPaperEntity paper = buildPaper(req, totalScore);
        paper.setCreatedBy(teacherId);
        examMapper.insertPaper(paper);
        savePaperQuestions(paper.getId(), req.getQuestions());
        return paper.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updatePaper(Long id, ExamPaperReq req) {
        ensurePaper(id);
        if (examMapper.countPaperSchedules(id) > 0) {
            throw new BusinessException(400, "试卷已经用于考试，不能再修改题目");
        }
        BigDecimal totalScore = validateAndCalculatePaper(req);
        ExamPaperEntity paper = buildPaper(req, totalScore);
        paper.setId(id);
        examMapper.updatePaper(paper);
        examMapper.deletePaperQuestions(id);
        savePaperQuestions(id, req.getQuestions());
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long deletePaper(Long id) {
        ensurePaper(id);
        if (examMapper.countPaperSchedules(id) > 0) {
            throw new BusinessException(400, "试卷已经用于考试，不能删除");
        }
        examMapper.deletePaperQuestions(id);
        examMapper.deletePaper(id);
        return id;
    }

    @Override
    public ExamPaperDetailRes paperDetail(Long id) {
        ExamPaperDetailRes result = new ExamPaperDetailRes();
        result.setPaper(ensurePaper(id));
        result.setQuestions(examMapper.selectPaperQuestions(id));
        return result;
    }

    @Override
    public PageResult<ExamPaperEntity> paperPage(ExamPaperPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<ExamPaperEntity> list = examMapper.selectPaperPage(req.getKeyword(), req.getStatus());
        PageInfo<ExamPaperEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(ExamScheduleReq req, Long teacherId) {
        validateSchedule(req);
        ExamPaperEntity paper = ensurePaper(req.getPaperId());
        if (!"ENABLED".equals(paper.getStatus())) {
            throw new BusinessException(400, "只能发布已启用的试卷");
        }
        ExamScheduleEntity entity = new ExamScheduleEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setStatus("PUBLISHED");
        entity.setAnswerPublished(false);
        entity.setCreatedBy(teacherId);
        examMapper.insertSchedule(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateSchedule(Long id, ExamScheduleReq req) {
        ensureSchedule(id);
        ensureNoAttempts(id, "考试已经有学员参加，不能修改");
        validateSchedule(req);
        ExamPaperEntity paper = ensurePaper(req.getPaperId());
        if (!"ENABLED".equals(paper.getStatus())) {
            throw new BusinessException(400, "只能使用已启用的试卷");
        }
        ExamScheduleEntity entity = new ExamScheduleEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setId(id);
        examMapper.updateSchedule(entity);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long deleteSchedule(Long id) {
        ensureSchedule(id);
        ensureNoAttempts(id, "考试已经有学员参加，不能删除");
        examMapper.deleteSchedule(id);
        return id;
    }

    @Override
    public Long publishAnswers(Long id) {
        ensureSchedule(id);
        examMapper.publishAnswers(id);
        return id;
    }

    @Override
    public PageResult<ExamScheduleEntity> schedulePage(ExamSchedulePageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<ExamScheduleEntity> list = examMapper.selectSchedulePage(
                req.getKeyword(), req.getClassId(), req.getStatus());
        PageInfo<ExamScheduleEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), list);
    }

    @Override
    public List<ExamAttemptEntity> attempts(Long scheduleId) {
        ensureSchedule(scheduleId);
        return examMapper.selectAttemptsBySchedule(scheduleId);
    }

    @Override
    public List<ExamAnswerEntity> attemptAnswers(Long attemptId) {
        ensureAttempt(attemptId);
        return examMapper.selectAnswersByAttempt(attemptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reviewAnswer(Long answerId, ExamReviewReq req, Long teacherId) {
        ExamAnswerEntity answer = examMapper.selectAnswerById(answerId);
        if (answer == null) {
            throw new BusinessException(404, "答题记录不存在");
        }
        if (!isSubjective(answer.getQuestionType())) {
            throw new BusinessException(400, "客观题由系统自动判分");
        }
        if (req.getScore().compareTo(answer.getQuestionScore()) > 0) {
            throw new BusinessException(400, "得分不能超过本题满分");
        }

        ExamAttemptEntity attempt = ensureAttempt(answer.getAttemptId());
        if ("IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BusinessException(400, "学员尚未交卷，不能批改");
        }

        examMapper.reviewAnswer(answerId, req.getScore(), req.getReviewComment(), teacherId);
        examMapper.refreshAttemptScore(answer.getAttemptId());

        /*
         * 当所有主观题都已经有分数时，考试记录改为 REVIEWED。
         * 没有主观题的试卷会在学员交卷时直接完成自动判分。
         */
        List<ExamAnswerEntity> answers = examMapper.selectAnswersByAttempt(answer.getAttemptId());
        boolean allReviewed = answers.stream()
                .filter(item -> isSubjective(item.getQuestionType()))
                .allMatch(item -> item.getScore() != null);
        if (allReviewed) {
            examMapper.markAttemptReviewed(answer.getAttemptId());
        }
        return answerId;
    }

    /**
     * 校验题型与选项。
     */
    private void validateQuestion(ExamQuestionReq req) {
        Set<String> supportedTypes = Set.of("SINGLE", "MULTIPLE", "JUDGE", "FILL", "SHORT");
        if (!supportedTypes.contains(req.getQuestionType())) {
            throw new BusinessException(400, "不支持的题型");
        }
        if (("SINGLE".equals(req.getQuestionType()) || "MULTIPLE".equals(req.getQuestionType()))
                && (req.getOptionsJson() == null || req.getOptionsJson().isBlank())) {
            throw new BusinessException(400, "选择题必须填写选项");
        }
        if (req.getCorrectAnswer() == null || req.getCorrectAnswer().isBlank()) {
            throw new BusinessException(400, "标准答案不能为空");
        }
    }

    /**
     * 校验试卷题目，并计算总分。
     */
    private BigDecimal validateAndCalculatePaper(ExamPaperReq req) {
        Set<Long> questionIds = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (ExamPaperQuestionReq question : req.getQuestions()) {
            ensureQuestion(question.getQuestionId());
            if (!questionIds.add(question.getQuestionId())) {
                throw new BusinessException(400, "同一道题不能重复加入试卷");
            }
            total = total.add(question.getQuestionScore());
        }
        if (req.getPassScore().compareTo(total) > 0) {
            throw new BusinessException(400, "及格分不能超过试卷总分");
        }
        return total;
    }

    /**
     * 保存试卷题目关联。
     */
    private void savePaperQuestions(Long paperId, List<ExamPaperQuestionReq> questions) {
        for (ExamPaperQuestionReq req : questions) {
            ExamPaperQuestionEntity entity = new ExamPaperQuestionEntity();
            BeanUtils.copyProperties(req, entity);
            entity.setPaperId(paperId);
            examMapper.insertPaperQuestion(entity);
        }
    }

    /**
     * 构造试卷实体。
     */
    private ExamPaperEntity buildPaper(ExamPaperReq req, BigDecimal totalScore) {
        ExamPaperEntity entity = new ExamPaperEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setTotalScore(totalScore);
        return entity;
    }

    /**
     * 校验考试时间范围。
     */
    private void validateSchedule(ExamScheduleReq req) {
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new BusinessException(400, "考试截止时间必须晚于开始时间");
        }
    }

    /** 查询题目，不存在时给出统一提示。 */
    private ExamQuestionEntity ensureQuestion(Long id) {
        ExamQuestionEntity entity = examMapper.selectQuestionById(id);
        if (entity == null) {
            throw new BusinessException(404, "题目不存在");
        }
        return entity;
    }

    /** 查询试卷，不存在时给出统一提示。 */
    private ExamPaperEntity ensurePaper(Long id) {
        ExamPaperEntity entity = examMapper.selectPaperById(id);
        if (entity == null) {
            throw new BusinessException(404, "试卷不存在");
        }
        return entity;
    }

    /** 查询考试发布记录，不存在时给出统一提示。 */
    private ExamScheduleEntity ensureSchedule(Long id) {
        ExamScheduleEntity entity = examMapper.selectScheduleById(id);
        if (entity == null) {
            throw new BusinessException(404, "考试不存在");
        }
        return entity;
    }

    /** 查询学员考试记录，不存在时给出统一提示。 */
    private ExamAttemptEntity ensureAttempt(Long id) {
        ExamAttemptEntity entity = examMapper.selectAttemptById(id);
        if (entity == null) {
            throw new BusinessException(404, "考试记录不存在");
        }
        return entity;
    }

    /** 确认考试尚无人参加。 */
    private void ensureNoAttempts(Long scheduleId, String message) {
        if (!examMapper.selectAttemptsBySchedule(scheduleId).isEmpty()) {
            throw new BusinessException(400, message);
        }
    }

    /** 判断是否为需要老师批改的主观题。 */
    private boolean isSubjective(String questionType) {
        return "FILL".equals(questionType) || "SHORT".equals(questionType);
    }
}
