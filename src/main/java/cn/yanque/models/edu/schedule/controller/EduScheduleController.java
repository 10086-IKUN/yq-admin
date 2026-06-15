package cn.yanque.models.edu.schedule.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.edu.schedule.pojo.entity.EduClassScheduleEntity;
import cn.yanque.models.edu.schedule.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.models.edu.schedule.pojo.vo.req.SchedulePageReq;
import cn.yanque.models.edu.schedule.pojo.vo.res.ScheduleGenerateRes;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.edu.schedule.service.EduScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 课表管理控制器
 */
@RestController
@RequestMapping("/api/schedule")
@Slf4j
@Tag(name = "EduScheduleController", description = "课表管理")
public class EduScheduleController {

    @Autowired
    private EduScheduleService eduScheduleService;

    @PostMapping("/{classId}/generate")
    @Operation(description = "生成班级课表")
    @RequirePermission("class:update")
    public ApiResponse<ScheduleGenerateRes> generateSchedule(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @RequestBody ScheduleGenerateReq req) {
        return ApiResponse.success(eduScheduleService.generateSchedule(classId, req));
    }

    @GetMapping
    @Operation(description = "分页查询课表")
    @RequirePermission("class:view")
    public ApiResponse<PageResult<SchedulePageRes>> pageSchedule(@ModelAttribute SchedulePageReq req) {
        return ApiResponse.success(eduScheduleService.pageSchedule(req));
    }

    @GetMapping("/{classId}/busyTeachers")
    @Operation(description = "查询已排课老师")
    @RequirePermission("class:view")
    public ApiResponse<List<Long>> getBusyTeachers(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ApiResponse.success(eduScheduleService.getBusyTeacherIds(classId, startDate, endDate));
    }

    @PutMapping("/{id}")
    @Operation(description = "更新课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> updateSchedule(
            @Parameter(description = "课表ID") @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> params) {
        String scheduleType = params.get("scheduleType") != null ? params.get("scheduleType").toString() : null;
        String courseContent = params.get("courseContent") != null ? params.get("courseContent").toString() : null;
        Long teacherId = params.get("teacherId") != null ? Long.valueOf(params.get("teacherId").toString()) : null;
        String stageName = params.get("stageName") != null ? params.get("stageName").toString() : null;
        eduScheduleService.updateSchedule(id, scheduleType, courseContent, teacherId, stageName);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(description = "删除课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> deleteSchedule(
            @Parameter(description = "课表ID") @PathVariable Long id,
            @Parameter(description = "是否前移后续课程") @RequestParam(defaultValue = "false") boolean forwardCourses) {
        eduScheduleService.deleteSchedule(id, forwardCourses);
        return ApiResponse.success();
    }

    @GetMapping("/busyTeachersByDate")
    @Operation(description = "查询指定日期已排课老师")
    @RequirePermission("class:view")
    public ApiResponse<List<Long>> getBusyTeachersByDate(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        return ApiResponse.success(eduScheduleService.getBusyTeacherIdsByDate(scheduleDate));
    }

    @PostMapping("/insert")
    @Operation(description = "插入课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> insertSchedule(
            @RequestBody EduClassScheduleEntity entity,
            @Parameter(description = "插入日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        eduScheduleService.insertSchedule(entity, scheduleDate);
        return ApiResponse.success();
    }
}
