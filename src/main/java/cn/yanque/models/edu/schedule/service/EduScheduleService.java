package cn.yanque.models.edu.schedule.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.edu.schedule.pojo.entity.EduClassScheduleEntity;
import cn.yanque.models.edu.schedule.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.models.edu.schedule.pojo.vo.req.SchedulePageReq;
import cn.yanque.models.edu.schedule.pojo.vo.res.ScheduleGenerateRes;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;

import java.util.Date;
import java.util.List;

/**
 * 课表服务接口
 */
public interface EduScheduleService {

    ScheduleGenerateRes generateSchedule(Long classId, ScheduleGenerateReq req);

    PageResult<SchedulePageRes> pageSchedule(SchedulePageReq req);

    List<Long> getBusyTeacherIds(Long classId, Date startDate, Date endDate);

    void deleteSchedule(Long scheduleId, boolean forwardCourses);

    List<Long> getBusyTeacherIdsByDate(Date scheduleDate);

    void insertSchedule(EduClassScheduleEntity entity, Date scheduleDate);

    void updateSchedule(Long scheduleId, String scheduleType, String courseContent, Long teacherId, String stageName);
}
