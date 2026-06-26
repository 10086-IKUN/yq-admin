package cn.yanque.models.studentFront.service.impl;

import cn.yanque.models.exam.mapper.ExamMapper;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.homework.mapper.HomeworkAssignmentMapper;
import cn.yanque.models.homework.mapper.HomeworkSubmissionMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreOverviewRes;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import cn.yanque.models.studentFront.service.StudentScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 学员端成绩服务实现类。
 *
 * <p>课程成绩不是单独维护一张结果表，而是从已经可以展示给学员的作业成绩和考试成绩中实时汇总。
 * 当前计算规则是：作业平均分占 40%，考试平均分占 60%。</p>
 */
@Service
public class StudentScoreServiceImpl implements StudentScoreService {

    /** 作业成绩权重。 */
    private static final BigDecimal HOMEWORK_WEIGHT = new BigDecimal("0.4");

    /** 考试成绩权重。 */
    private static final BigDecimal EXAM_WEIGHT = new BigDecimal("0.6");

    /** 作业表没有满分字段，当前先按 100 分制展示。 */
    private static final BigDecimal DEFAULT_HOMEWORK_FULL_SCORE = new BigDecimal("100");

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Autowired
    private HomeworkAssignmentMapper homeworkAssignmentMapper;

    @Autowired
    private ExamMapper examMapper;

    /**
     * 获取当前登录学员的成绩明细列表。
     *
     * @param studentId 当前登录学员ID，用于查询考试记录
     * @param studentNo 当前登录学号，用于查询作业提交记录
     * @return 作业成绩和考试成绩合并后的列表，按出分时间倒序排列
     */
    @Override
    public List<StudentScoreRes> list(Long studentId, String studentNo) {
        StudentScoreOverviewRes overview = overview(studentId, studentNo);
        List<StudentScoreRes> result = new ArrayList<>();
        result.addAll(overview.getHomeworkScores());
        result.addAll(overview.getExamScores());
        result.sort(Comparator.comparing(StudentScoreRes::getScoreTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    /**
     * 获取当前登录学员的课程综合成绩。
     *
     * <p>只有“已经允许学员查看”的成绩才参与计算：
     * 作业要求提交状态为 REVIEWED；考试要求试卷已经批改完成，并且结果可见。
     * 如果作业或考试暂时没有成绩，该分类平均分按 0 参与综合分计算。</p>
     *
     * @param studentId 当前登录学员ID，用于查询考试记录
     * @param studentNo 当前登录学号，用于查询作业提交记录
     * @return 课程综合成绩、作业成绩和考试成绩
     */
    @Override
    public StudentScoreOverviewRes overview(Long studentId, String studentNo) {
        List<StudentScoreRes> homeworkScores = buildHomeworkScores(studentNo);
        List<StudentScoreRes> examScores = buildExamScores(studentId);

        BigDecimal homeworkAverage = average(homeworkScores);
        BigDecimal examAverage = average(examScores);
        BigDecimal composite = homeworkAverage.multiply(HOMEWORK_WEIGHT)
                .add(examAverage.multiply(EXAM_WEIGHT))
                .setScale(2, RoundingMode.HALF_UP);

        StudentScoreOverviewRes res = new StudentScoreOverviewRes();
        res.setHomeworkScores(homeworkScores);
        res.setExamScores(examScores);
        res.setHomeworkAverageScore(homeworkAverage);
        res.setExamAverageScore(examAverage);
        res.setCompositeScore(composite);
        res.setPublishedScoreCount(homeworkScores.size() + examScores.size());
        res.setBestScore(bestScore(homeworkScores, examScores));
        return res;
    }

    /**
     * 构建作业成绩明细。
     *
     * <p>学员端只展示已经批改完成的作业成绩。
     * 未批改作业即使已经提交，也不能进入课程成绩计算。</p>
     *
     * @param studentNo 当前登录学号
     * @return 作业成绩明细
     */
    private List<StudentScoreRes> buildHomeworkScores(String studentNo) {
        return homeworkSubmissionMapper.selectByStudentNo(studentNo).stream()
                .filter(item -> "REVIEWED".equals(item.getStatus()))
                .filter(item -> item.getScore() != null)
                .map(this::toHomeworkScore)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 构建考试成绩明细。
     *
     * <p>填空题和简答题需要老师人工批改。
     * 因此考试成绩必须等整张卷子 REVIEWED 且 resultVisible 为 true 后，才展示给学员并参与综合分。</p>
     *
     * @param studentId 当前登录学员ID
     * @return 考试成绩明细
     */
    private List<StudentScoreRes> buildExamScores(Long studentId) {
        return examMapper.selectStudentExams(studentId).stream()
                .filter(item -> "REVIEWED".equals(item.getAttemptStatus()))
                .filter(item -> Boolean.TRUE.equals(item.getResultVisible()))
                .filter(item -> item.getStudentScore() != null)
                .map(this::toExamScore)
                .toList();
    }

    /**
     * 把作业提交记录转换成成绩明细。
     *
     * <p>提交记录里只有 assignmentId，所以需要再查作业发布表补齐标题和课程名称。</p>
     *
     * @param submission 作业提交记录
     * @return 作业成绩明细
     */
    private StudentScoreRes toHomeworkScore(HomeworkSubmissionEntity submission) {
        HomeworkAssignmentEntity assignment = homeworkAssignmentMapper.selectById(submission.getAssignmentId());
        if (assignment == null) {
            return null;
        }

        StudentScoreRes res = new StudentScoreRes();
        res.setScoreType("HOMEWORK");
        res.setTitle(assignment.getTitle());
        res.setCourseName(assignment.getCourseName());
        res.setScore(submission.getScore());
        res.setFullScore(DEFAULT_HOMEWORK_FULL_SCORE);
        res.setScoreTime(submission.getReviewTime());
        res.setStatus("已批改");
        res.setRemark(submission.getTeacherComment());
        return res;
    }

    /**
     * 把考试发布记录转换成成绩明细。
     *
     * <p>selectStudentExams 已经联表带回考试名称、试卷名称、试卷总分和当前学员得分。</p>
     *
     * @param exam 学员考试记录视图
     * @return 考试成绩明细
     */
    private StudentScoreRes toExamScore(ExamScheduleEntity exam) {
        StudentScoreRes res = new StudentScoreRes();
        res.setScoreType("EXAM");
        res.setTitle(exam.getExamName());
        res.setCourseName(exam.getPaperName());
        res.setScore(exam.getStudentScore());
        res.setFullScore(exam.getTotalScore());
        res.setScoreTime(exam.getEndTime());
        res.setStatus("已出分");
        res.setRemark(exam.getPaperName());
        return res;
    }

    /**
     * 计算平均分。
     *
     * @param scores 已经允许学员查看的成绩明细
     * @return 平均分；没有成绩时返回 0
     */
    private BigDecimal average(List<StudentScoreRes> scores) {
        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = scores.stream()
                .map(StudentScoreRes::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算最高单项成绩。
     *
     * @param homeworkScores 作业成绩
     * @param examScores 考试成绩
     * @return 最高成绩；没有成绩时返回 0
     */
    private BigDecimal bestScore(List<StudentScoreRes> homeworkScores, List<StudentScoreRes> examScores) {
        return List.of(homeworkScores, examScores).stream()
                .flatMap(List::stream)
                .map(StudentScoreRes::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
