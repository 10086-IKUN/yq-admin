package cn.yanque.models.studentVisit.service;

import cn.yanque.models.studentVisit.pojo.entity.StudentVisitEntity;

import java.util.List;

public interface StudentVisitService {

    /**
     * 获取今日需回访的学员列表
     */
    List<StudentVisitEntity> getTodayVisitList(Long teacherId);

    /**
     * 提交回访记录
     */
    void submitVisit(StudentVisitEntity entity);

    /**
     * 查询学员回访历史
     */
    List<StudentVisitEntity> getHistory(Long studentId);

    /**
     * 初始化学员回访计划
     */
    void initVisitPlan(Long studentId, Long teacherId, String tagType);

    /**
     * 根据标签获取回访天数
     */
    int getVisitDays(String tagType);
}
