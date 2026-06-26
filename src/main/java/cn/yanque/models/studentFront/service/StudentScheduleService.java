package cn.yanque.models.studentFront.service;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;

import java.util.List;

/**
 * 学员端课表服务接口。
 *
 * <p>服务层负责根据学员ID查询学生绑定班级，再返回该班级的课表数据。</p>
 */
public interface StudentScheduleService {

    /**
     * 获取当前登录学员的个人课表。
     *
     * @param studentId 当前登录学员ID，来自学员端 token
     * @return 当前学员所在班级的课表列表
     */
    List<SchedulePageRes> list(Long studentId);
}
