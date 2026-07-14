package cn.yanque.models.studentFront.ai.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentPageReq;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentPageRes;
import cn.yanque.models.duty.service.EduDutyAssignmentService;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.service.StudentScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Python AI 工具内部接口。
 *
 * <p>这些接口不直接暴露给前端，只给 Python AI 服务执行工具时调用。Python 不读业务库，
 * 课程权限、班级归属和课表规则仍然在 Java 业务层完成。</p>
 */
@RestController
@RequestMapping("/internal/ai/tools")
@Tag(name = "InternalAiToolController", description = "AI 内部工具接口")
public class InternalAiToolController {

    @Autowired
    private StudentScheduleService studentScheduleService;

    @Autowired
    private EduDutyAssignmentService dutyAssignmentService;

    @GetMapping("/student-day-schedule")
    @Operation(description = "查询当前学员指定日期课表")
    public ApiResponse<Map<String, Object>> studentDaySchedule(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestHeader(value = "X-Internal-Token", required = false) String internalToken) {
        List<SchedulePageRes> schedules = studentScheduleService.listByDate(studentId, date);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("found", !schedules.isEmpty());
        data.put("scheduleDate", formatDate(date));
        data.put("weekday", weekday(date));
        data.put("total", schedules.size());
        data.put("items", schedules.stream().map(this::toScheduleItem).toList());
        if (schedules.isEmpty()) {
            data.put("message", "当天没有课程安排");
        }
        return ApiResponse.success(data);
    }

    @GetMapping("/day-duty")
    @Operation(description = "查询指定日期值班安排")
    public ApiResponse<Map<String, Object>> dayDuty(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestHeader(value = "X-Internal-Token", required = false) String internalToken) {
        DutyAssignmentPageReq req = new DutyAssignmentPageReq();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        req.setStartDate(sqlDate);
        req.setEndDate(sqlDate);
        req.setPageNum(1);
        req.setPageSize(200);

        PageResult<DutyAssignmentPageRes> page = dutyAssignmentService.pageDutyAssignment(req);
        List<DutyAssignmentPageRes> duties = page.getRecords() == null ? List.of() : page.getRecords();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("found", !duties.isEmpty());
        data.put("dutyDate", formatDate(date));
        data.put("weekday", weekday(date));
        data.put("total", duties.size());
        data.put("items", duties.stream().map(this::toDutyItem).toList());
        if (duties.isEmpty()) {
            data.put("message", "当天没有值班信息");
        }
        return ApiResponse.success(data);
    }

    private Map<String, Object> toScheduleItem(SchedulePageRes schedule) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", schedule.getId());
        item.put("className", schedule.getClassName());
        item.put("courseName", schedule.getCourseName());
        item.put("courseContent", schedule.getCourseContent());
        item.put("courseDayNum", schedule.getCourseDayNum());
        item.put("scheduleType", schedule.getScheduleType());
        item.put("stageName", schedule.getStageName());
        item.put("teacherName", schedule.getTeacherName());
        return item;
    }

    private Map<String, Object> toDutyItem(DutyAssignmentPageRes duty) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", duty.getId());
        item.put("dutyType", duty.getDutyType());
        item.put("className", duty.getClassName());
        item.put("startTime", duty.getStartTime());
        item.put("endTime", duty.getEndTime());
        item.put("teacherName", duty.getTeacherName());
        item.put("remark", duty.getRemark());
        return item;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String weekday(Date date) {
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> "星期一";
            case TUESDAY -> "星期二";
            case WEDNESDAY -> "星期三";
            case THURSDAY -> "星期四";
            case FRIDAY -> "星期五";
            case SATURDAY -> "星期六";
            case SUNDAY -> "星期日";
        };
    }
}
