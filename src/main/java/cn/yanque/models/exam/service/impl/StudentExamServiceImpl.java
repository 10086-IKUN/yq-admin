package cn.yanque.models.exam.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.exam.mapper.ExamMapper;
import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamAnswerSaveItemReq;
import cn.yanque.models.exam.pojo.vo.req.ExamAnswerSaveReq;
import cn.yanque.models.exam.pojo.vo.res.StudentExamDetailRes;
import cn.yanque.models.exam.service.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学员端在线考试服务实现。
 *
 * <p>核心约束：
 * 每名学员对每场考试只有一条 exam_attempt；
 * 交卷后不允许再次保存或提交；
 * 成绩在答案发布或考试截止后才返回。</p>
 */
@Service
public class StudentExamServiceImpl implements StudentExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Override
    public List<ExamScheduleEntity> list(Long studentId) {
        List<ExamScheduleEntity> exams = examMapper.selectStudentExams(studentId);
        exams.forEach(exam -> {
            /*
             * 列表页成绩可见规则和详情页保持一致。
             * 只有当前学员这份试卷进入 REVIEWED，才说明填空题、简答题也已批改完成。
             */
            boolean visible = "REVIEWED".equals(exam.getAttemptStatus());
            exam.setResultVisible(visible);
            if (!visible) {
                exam.setStudentScore(null);
            }
        });
        return exams;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentExamDetailRes detail(Long scheduleId, Long studentId) {
        ExamScheduleEntity exam = ensureStudentExam(scheduleId, studentId);
        ExamAttemptEntity attempt = examMapper.selectAttemptByStudent(scheduleId, studentId);

        /*
         * 学员离开页面后可能错过前端自动交卷。
         * 再次查看详情时，如果答题时间已经结束，后端会补做一次交卷，
         * 防止考试记录永久停留在 IN_PROGRESS。
         */
        if (attempt != null && "IN_PROGRESS".equals(attempt.getStatus()) && isExpired(exam, attempt)) {
            submitInternal(attempt);
            attempt = examMapper.selectAttemptById(attempt.getId());
        }
        return buildDetail(exam, attempt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentExamDetailRes start(Long scheduleId, Long studentId) {
        ExamScheduleEntity exam = ensureStudentExam(scheduleId, studentId);
        ExamAttemptEntity existing = examMapper.selectAttemptByStudent(scheduleId, studentId);
        if (existing != null) {
            if (!"IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessException(400, "该考试已经交卷，不能重复作答");
            }
            if (isExpired(exam, existing)) {
                submitInternal(existing);
                return buildDetail(exam, examMapper.selectAttemptById(existing.getId()));
            }
            return buildDetail(exam, existing);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new BusinessException(400, "考试尚未开始");
        }
        if (!now.isBefore(exam.getEndTime())) {
            throw new BusinessException(400, "考试已经截止");
        }
        if (!"PUBLISHED".equals(exam.getStatus())) {
            throw new BusinessException(400, "考试当前不可参加");
        }

        EduStudentEntity student = eduStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学员不存在");
        }

        ExamAttemptEntity attempt = new ExamAttemptEntity();
        attempt.setScheduleId(scheduleId);
        attempt.setStudentId(studentId);
        attempt.setStudentNo(student.getStudentCode());
        attempt.setStudentNameSnapshot(student.getStudentName());
        attempt.setStartTime(now);
        attempt.setStatus("IN_PROGRESS");
        examMapper.insertAttempt(attempt);
        examMapper.insertAttemptAnswers(attempt.getId(), exam.getPaperId());
        return buildDetail(exam, examMapper.selectAttemptById(attempt.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAnswers(Long attemptId, ExamAnswerSaveReq req, Long studentId) {
        ExamAttemptEntity attempt = ensureOwnAttempt(attemptId, studentId);
        ExamScheduleEntity exam = ensureStudentExam(attempt.getScheduleId(), studentId);
        ensureAnswering(exam, attempt);

        for (ExamAnswerSaveItemReq item : req.getAnswers()) {
            int affected = examMapper.updateAnswerContent(item.getAnswerId(), attemptId, item.getAnswerContent());
            if (affected == 0) {
                throw new BusinessException(400, "答案保存失败，请刷新后重试");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentExamDetailRes submit(Long attemptId, Long studentId) {
        ExamAttemptEntity attempt = ensureOwnAttempt(attemptId, studentId);
        ExamScheduleEntity exam = ensureStudentExam(attempt.getScheduleId(), studentId);
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BusinessException(400, "该考试已经交卷，不能重复提交");
        }
        submitInternal(attempt);
        return buildDetail(exam, examMapper.selectAttemptById(attemptId));
    }

    /**
     * 执行最终交卷和客观题自动判分。
     */
    private void submitInternal(ExamAttemptEntity attempt) {
        List<ExamAnswerEntity> answers = examMapper.selectAnswersByAttempt(attempt.getId());
        BigDecimal objectiveScore = BigDecimal.ZERO;
        boolean hasSubjective = false;

        for (ExamAnswerEntity answer : answers) {
            if (isSubjective(answer.getQuestionType())) {
                hasSubjective = true;
                continue;
            }

            boolean correct = normalizeAnswer(answer.getAnswerContent())
                    .equals(normalizeAnswer(answer.getCorrectAnswer()));
            BigDecimal score = correct ? answer.getQuestionScore() : BigDecimal.ZERO;
            examMapper.updateAnswerScore(answer.getId(), correct, score);
            objectiveScore = objectiveScore.add(score);
        }

        if (examMapper.submitAttempt(attempt.getId(), objectiveScore) == 0) {
            throw new BusinessException(400, "该考试已经交卷");
        }
        if (!hasSubjective) {
            examMapper.markAttemptReviewed(attempt.getId());
        }
    }

    /**
     * 构造学员端详情，并按成绩可见规则隐藏标准答案和分数。
     */
    private StudentExamDetailRes buildDetail(ExamScheduleEntity exam, ExamAttemptEntity attempt) {
        /*
         * 详情页结果可见规则：
         * 1. 只有当前学员这份试卷已经 REVIEWED，才说明整张卷子批改完成。
         * 2. SUBMITTED 只代表已交卷，填空题和简答题可能还没批改，不能展示任何得分。
         * 3. 发布答案只表示允许看标准答案，不代表当前学员试卷已经批改完成。
         */
        boolean resultVisible = attempt != null && "REVIEWED".equals(attempt.getStatus());
        exam.setResultVisible(resultVisible);

        StudentExamDetailRes result = new StudentExamDetailRes();
        result.setExam(exam);
        result.setAttempt(attempt);
        if (attempt == null) {
            result.setAnswers(List.of());
            return result;
        }

        List<ExamAnswerEntity> answers = examMapper.selectAnswersByAttempt(attempt.getId());
        if (!resultVisible) {
            attempt.setObjectiveScore(null);
            attempt.setSubjectiveScore(null);
            attempt.setTotalScore(null);
            answers.forEach(answer -> {
                answer.setCorrectAnswer(null);
                answer.setAnswerAnalysis(null);
                answer.setCorrect(null);
                answer.setScore(null);
                answer.setReviewComment(null);
            });
        }
        result.setAnswers(answers);
        return result;
    }

    /**
     * 确认当前考试属于学员所在班级。
     */
    private ExamScheduleEntity ensureStudentExam(Long scheduleId, Long studentId) {
        ExamScheduleEntity exam = examMapper.selectStudentExams(studentId)
                .stream()
                .filter(item -> Objects.equals(item.getId(), scheduleId))
                .findFirst()
                .orElse(null);
        if (exam == null) {
            throw new BusinessException(404, "考试不存在或不属于当前班级");
        }
        return exam;
    }

    /**
     * 确认考试记录属于当前登录学员。
     */
    private ExamAttemptEntity ensureOwnAttempt(Long attemptId, Long studentId) {
        ExamAttemptEntity attempt = examMapper.selectAttemptById(attemptId);
        if (attempt == null || !Objects.equals(attempt.getStudentId(), studentId)) {
            throw new BusinessException(404, "考试记录不存在");
        }
        return attempt;
    }

    /**
     * 确认仍处于答题状态和有效时间内。
     */
    private void ensureAnswering(ExamScheduleEntity exam, ExamAttemptEntity attempt) {
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BusinessException(400, "考试已经交卷，不能继续修改");
        }
        if (isExpired(exam, attempt)) {
            throw new BusinessException(400, "考试时间已经结束");
        }
    }

    /**
     * 判断考试是否达到发布截止时间或个人考试时长。
     */
    private boolean isExpired(ExamScheduleEntity exam, ExamAttemptEntity attempt) {
        LocalDateTime personalEnd = attempt.getStartTime().plusMinutes(exam.getDurationMinutes());
        LocalDateTime effectiveEnd = personalEnd.isBefore(exam.getEndTime()) ? personalEnd : exam.getEndTime();
        return !LocalDateTime.now().isBefore(effectiveEnd);
    }

    /**
     * 统一答案格式。
     *
     * <p>多选题会按选项排序后比较，因此“A,C”和“C,A”视为相同答案。
     * 普通答案会去除首尾空格并忽略大小写。</p>
     */
    private String normalizeAnswer(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(item -> !item.isEmpty())
                .sorted()
                .collect(Collectors.joining(","));
    }

    /** 判断是否为主观题。 */
    private boolean isSubjective(String questionType) {
        return "FILL".equals(questionType) || "SHORT".equals(questionType);
    }
}
