package cn.yanque.models.studentTag.service.impl;

import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.homework.mapper.HomeworkAssignmentMapper;
import cn.yanque.models.homework.mapper.HomeworkSubmissionMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.studentTag.mapper.StudentTagMapper;
import cn.yanque.models.studentTag.pojo.entity.StudentTagEntity;
import cn.yanque.models.studentTag.pojo.vo.StudentTagVO;
import cn.yanque.models.studentTag.service.StudentTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class StudentTagServiceImpl implements StudentTagService {

    /** 按时提交率达到 80% 及以上时，系统建议为积极学员。 */
    private static final BigDecimal ACTIVE_THRESHOLD = new BigDecimal("80");

    /** 按时提交率达到 50% 及以上时，系统建议为正常学员。 */
    private static final BigDecimal NORMAL_THRESHOLD = new BigDecimal("50");

    /** 没有布置作业时，学员没有可提交内容，默认按积极学员处理。 */
    private static final BigDecimal NO_HOMEWORK_RATE = new BigDecimal("100");

    @Autowired
    private StudentTagMapper studentTagMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Autowired
    private HomeworkAssignmentMapper homeworkAssignmentMapper;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    /**
     * 重新计算单个学员标签。
     *
     * <p>作业总数统计该学员所在班级下的全部未删除作业。
     * 如果班级没有布置任何作业，则写入 0/0，并把该学员视为积极学员。</p>
     *
     * @param studentId 学员ID
     */
    @Override
    public void calculateTag(Long studentId) {
        EduStudentEntity student = eduStudentMapper.selectById(studentId);
        if (student == null) {
            return;
        }

        /*
         * 这里不能使用 selectClosedByClassId。
         * 标签统计要求统计“发布给该班级的全部作业”，不是只统计已截止或已关闭作业。
         */
        List<HomeworkAssignmentEntity> assignments = homeworkAssignmentMapper.selectByClassId(student.getClassId());
        int totalAssignments = assignments.size();

        if (totalAssignments == 0) {
            saveTag(studentId, "ACTIVE", NO_HOMEWORK_RATE, 0, 0);
            log.info("学员标签计算完成: studentId={}, tagType=ACTIVE, totalAssignments=0", studentId);
            return;
        }

        int onTimeCount = calculateOnTimeCount(student, assignments);
        BigDecimal onTimeRate = new BigDecimal(onTimeCount)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
        String tagType = determineTagType(onTimeRate);

        saveTag(studentId, tagType, onTimeRate, totalAssignments, onTimeCount);
        log.info("学员标签计算完成: studentId={}, tagType={}, onTimeRate={}%", studentId, tagType, onTimeRate);
    }

    /**
     * 批量重新计算所有学员标签。
     *
     * <p>这里遍历 edu_student 全量学员。
     * 即使某个学员所在班级没有布置作业，也会生成积极学员标签，避免列表漏人。</p>
     */
    @Override
    public void calculateAllTags() {
        log.info("开始批量计算学员标签...");
        List<EduStudentEntity> students = eduStudentMapper.selectAll();
        int count = 0;
        for (EduStudentEntity student : students) {
            try {
                calculateTag(student.getId());
                count++;
            } catch (Exception e) {
                log.error("学员标签计算失败: studentId={}", student.getId(), e);
            }
        }
        log.info("学员标签计算完成，共同步{}条记录", count);
    }

    @Override
    public StudentTagEntity getByStudentId(Long studentId) {
        return studentTagMapper.selectByStudentId(studentId);
    }

    @Override
    public List<StudentTagVO> list(String tagType, String keyword) {
        return studentTagMapper.selectList(tagType, keyword);
    }

    @Override
    public void confirm(Long id, Long confirmedBy, String tagType) {
        studentTagMapper.confirm(id, confirmedBy, tagType);
    }

    @Override
    public List<StudentTagVO> listByTeacherId(Long teacherId) {
        return studentTagMapper.selectByTeacherId(teacherId);
    }

    /**
     * 计算学员按时提交的作业数量。
     *
     * <p>只有存在提交记录、提交时间不为空，并且提交时间不晚于作业截止时间，才算按时提交。</p>
     *
     * @param student 学员信息
     * @param assignments 该学员所在班级的全部作业
     * @return 按时提交数量
     */
    private int calculateOnTimeCount(EduStudentEntity student, List<HomeworkAssignmentEntity> assignments) {
        List<HomeworkSubmissionEntity> submissions = homeworkSubmissionMapper.selectByStudentNo(student.getStudentCode());
        int onTimeCount = 0;

        for (HomeworkAssignmentEntity assignment : assignments) {
            HomeworkSubmissionEntity submission = submissions.stream()
                    .filter(item -> item.getAssignmentId().equals(assignment.getId()))
                    .findFirst()
                    .orElse(null);

            if (submission != null
                    && submission.getSubmitTime() != null
                    && assignment.getDeadline() != null
                    && !submission.getSubmitTime().isAfter(assignment.getDeadline())) {
                onTimeCount++;
            }
        }
        return onTimeCount;
    }

    /**
     * 保存或更新学员标签。
     *
     * <p>student_tag 表历史上不一定给 student_id 建了唯一键。
     * 所以这里显式先更新再插入，避免 ON DUPLICATE KEY 不生效时产生重复旧记录。</p>
     *
     * @param studentId 学员ID
     * @param tagType 标签类型
     * @param onTimeRate 按时提交率
     * @param totalAssignments 作业总数
     * @param onTimeCount 按时提交数量
     */
    private void saveTag(Long studentId, String tagType, BigDecimal onTimeRate, int totalAssignments, int onTimeCount) {
        StudentTagEntity tag = new StudentTagEntity();
        tag.setStudentId(studentId);
        tag.setTagType(tagType);
        tag.setOnTimeRate(onTimeRate);
        tag.setTotalAssignments(totalAssignments);
        tag.setOnTimeCount(onTimeCount);

        int updated = studentTagMapper.updateByStudentId(tag);
        if (updated == 0) {
            studentTagMapper.insert(tag);
        }
    }

    /**
     * 根据按时提交率确定标签类型。
     *
     * @param onTimeRate 按时提交率
     * @return 标签类型
     */
    private String determineTagType(BigDecimal onTimeRate) {
        if (onTimeRate.compareTo(ACTIVE_THRESHOLD) >= 0) {
            return "ACTIVE";
        }
        if (onTimeRate.compareTo(NORMAL_THRESHOLD) >= 0) {
            return "NORMAL";
        }
        return "LAZY";
    }
}
