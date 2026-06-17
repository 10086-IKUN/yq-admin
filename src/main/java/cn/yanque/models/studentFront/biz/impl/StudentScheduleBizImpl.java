package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.biz.StudentScheduleBiz;
import cn.yanque.models.studentFront.service.StudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学员端课表业务实现类
 * 实现学员端课表相关的业务逻辑
 */
@Component
public class StudentScheduleBizImpl implements StudentScheduleBiz {

    @Autowired
    private StudentScheduleService studentScheduleService;

    /**
     * 获取个人课表
     * @return 课表列表
     */
    @Override
    public List<SchedulePageRes> list() {
        return studentScheduleService.list();
    }
}
