package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.models.studentFront.biz.StudentScoreBiz;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreOverviewRes;
import cn.yanque.models.studentFront.service.StudentScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学员端成绩业务实现类
 * 实现学员端成绩相关的业务逻辑
 */
@Component
public class StudentScoreBizImpl implements StudentScoreBiz {

    @Autowired
    private StudentScoreService studentScoreService;

    /**
     * 获取当前登录学员的成绩列表。
     *
     * @param studentId 当前登录学员ID
     * @param studentNo 当前登录学号
     * @return 作业和考试合并后的成绩列表
     */
    @Override
    public List<StudentScoreRes> list(Long studentId, String studentNo) {
        return studentScoreService.list(studentId, studentNo);
    }

    /**
     * 获取当前登录学员的课程综合成绩。
     *
     * @param studentId 当前登录学员ID
     * @param studentNo 当前登录学号
     * @return 课程综合成绩、作业成绩和考试成绩
     */
    @Override
    public StudentScoreOverviewRes overview(Long studentId, String studentNo) {
        return studentScoreService.overview(studentId, studentNo);
    }
}
