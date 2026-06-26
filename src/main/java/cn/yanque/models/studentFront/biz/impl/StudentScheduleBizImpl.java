package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.biz.StudentScheduleBiz;
import cn.yanque.models.studentFront.service.StudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学员端课表业务实现类。
 *
 * <p>当前业务比较直接：把当前登录学员ID传给服务层，由服务层完成学生、班级、课表的数据查询。</p>
 */
@Component
public class StudentScheduleBizImpl implements StudentScheduleBiz {

    @Autowired
    private StudentScheduleService studentScheduleService;

    /**
     * 获取当前登录学员的个人课表。
     *
     * @param studentId 当前登录学员ID，来自学员端 token
     * @return 当前学员所在班级的课表列表
     */
    @Override
    public List<SchedulePageRes> list(Long studentId) {
        return studentScheduleService.list(studentId);
    }
}
