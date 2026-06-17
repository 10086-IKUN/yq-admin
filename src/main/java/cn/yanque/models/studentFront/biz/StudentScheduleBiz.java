package cn.yanque.models.studentFront.biz;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;

import java.util.List;

/**
 * 学员端课表业务接口
 * 定义学员端课表相关的业务逻辑方法
 */
public interface StudentScheduleBiz {

    /**
     * 获取个人课表
     * @return 课表列表
     */
    List<SchedulePageRes> list();
}
