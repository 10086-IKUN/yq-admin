package cn.yanque.models.studentFront.biz;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;

import java.util.List;

/**
 * 学员端课表业务接口。
 *
 * <p>业务层负责接收当前登录学员ID，并保证只能查询该学员所属班级的课表。</p>
 */
public interface StudentScheduleBiz {

    /**
     * 获取当前登录学员的个人课表。
     *
     * @param studentId 当前登录学员ID，来自学员端 token
     * @return 当前学员所在班级的课表列表
     */
    List<SchedulePageRes> list(Long studentId);
}
