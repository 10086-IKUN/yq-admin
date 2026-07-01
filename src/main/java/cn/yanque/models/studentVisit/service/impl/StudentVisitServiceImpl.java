package cn.yanque.models.studentVisit.service.impl;

import cn.yanque.models.studentVisit.mapper.StudentVisitMapper;
import cn.yanque.models.studentVisit.pojo.entity.StudentVisitEntity;
import cn.yanque.models.studentVisit.service.StudentVisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StudentVisitServiceImpl implements StudentVisitService {

    @Autowired
    private StudentVisitMapper studentVisitMapper;

    private static final int ACTIVE_VISIT_DAYS = 3;
    private static final int NORMAL_VISIT_DAYS = 5;
    private static final int LAZY_VISIT_DAYS = 7;

    @Override
    public List<StudentVisitEntity> getTodayVisitList(Long teacherId) {
        return studentVisitMapper.selectTodayVisit(teacherId, LocalDate.now());
    }

    @Override
    public void submitVisit(StudentVisitEntity entity) {
        StudentVisitEntity existing = studentVisitMapper.selectById(entity.getId());
        if (existing == null) {
            return;
        }

        entity.setStudentId(existing.getStudentId());
        entity.setVisitTime(LocalDateTime.now());
        entity.setStatus("VISITED");
        studentVisitMapper.update(entity);

        StudentVisitEntity nextVisit = new StudentVisitEntity();
        nextVisit.setStudentId(existing.getStudentId());
        nextVisit.setTeacherId(entity.getTeacherId());
        nextVisit.setNextVisitTime(LocalDate.now().plusDays(getVisitDays("NORMAL")));
        nextVisit.setStatus("PENDING");
        studentVisitMapper.insert(nextVisit);

        log.info("Student visit submitted: studentId={}, nextVisitTime={}",
                existing.getStudentId(), nextVisit.getNextVisitTime());
    }

    @Override
    public List<StudentVisitEntity> getHistory(Long studentId) {
        return studentVisitMapper.selectByStudentId(studentId);
    }

    @Override
    public void initVisitPlan(Long studentId, Long teacherId, String tagType) {
        StudentVisitEntity existing = studentVisitMapper.selectLatestPending(studentId);
        if (existing != null) {
            log.info("Student already has pending visit plan: studentId={}", studentId);
            return;
        }

        StudentVisitEntity visit = new StudentVisitEntity();
        visit.setStudentId(studentId);
        visit.setTeacherId(teacherId);
        visit.setNextVisitTime(LocalDate.now().plusDays(getVisitDays(tagType)));
        visit.setStatus("PENDING");
        studentVisitMapper.insert(visit);

        log.info("Student visit plan initialized: studentId={}, tagType={}, nextVisitTime={}",
                studentId, tagType, visit.getNextVisitTime());
    }

    @Override
    public int getVisitDays(String tagType) {
        if (tagType == null) {
            return NORMAL_VISIT_DAYS;
        }
        return switch (tagType) {
            case "ACTIVE" -> ACTIVE_VISIT_DAYS;
            case "LAZY" -> LAZY_VISIT_DAYS;
            default -> NORMAL_VISIT_DAYS;
        };
    }
}
