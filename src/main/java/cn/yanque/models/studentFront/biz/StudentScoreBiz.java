package cn.yanque.models.studentFront.biz;

import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreOverviewRes;

import java.util.List;

/**
 * 学员端成绩业务接口
 * 定义学员端成绩相关的业务逻辑方法
 */
public interface StudentScoreBiz {

    /**
     * 获取当前登录学员的成绩列表。
     *
     * @param studentId 当前登录学员ID
     * @param studentNo 当前登录学号
     * @return 作业和考试合并后的成绩列表
     */
    List<StudentScoreRes> list(Long studentId, String studentNo);

    /**
     * 获取当前登录学员的课程综合成绩。
     *
     * @param studentId 当前登录学员ID
     * @param studentNo 当前登录学号
     * @return 课程综合成绩、作业成绩和考试成绩
     */
    StudentScoreOverviewRes overview(Long studentId, String studentNo);
}
