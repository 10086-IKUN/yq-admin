package cn.yanque.models.studentFront.service;

import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;

import java.util.List;

/**
 * 学员端课表服务接口
 * 定义学员端课表相关的数据操作方法
 */
public interface StudentScheduleService {

    /**
     * 获取个人课表
     * @return 课表列表
     */
    List<SchedulePageRes> list();
}
