package cn.yanque.models.edu.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.entity.EduClassScheduleEntity;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.common.pojo.vo.req.SchedulePageReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;
import cn.yanque.common.pojo.vo.res.ScheduleGenerateRes;
import cn.yanque.common.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.edu.service.EduClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 班级管理控制器
 * 提供班级的增删改查接口
 */
@RestController
@RequestMapping("/api/eduClass")
@Slf4j
@Tag(name = "EduClassController", description = "班级管理")
public class EduClassController {

    @Autowired
    private EduClassService eduClassService;

    /**
     * 添加班级
     * @param req 创建班级请求参数
     * @return 创建成功的班级信息
     */
    @PostMapping
    @Operation(description = "添加班级")
    @RequirePermission("class:add")
    public ApiResponse<ClassCreateRes> addClass(@Valid @RequestBody ClassCreateReq req) {
        return ApiResponse.success(eduClassService.addClass(req));
    }

    /**
     * 修改班级
     * @param id 班级ID
     * @param req 更新班级请求参数
     * @return 更新后的班级信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改班级")
    @RequirePermission("class:update")
    public ApiResponse<ClassUpdateRes> updateClass(@Parameter(description = "班级ID") @PathVariable Long id,
                                                   @Valid @RequestBody ClassUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduClassService.updateClass(req));
    }

    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除班级")
    @RequirePermission("class:delete")
    public ApiResponse<ClassDeleteRes> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        return ApiResponse.success(eduClassService.deleteClass(id));
    }

    /**
     * 根据ID查询班级
     * @param id 班级ID
     * @return 班级详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询班级")
    @RequirePermission("class:view")
    public ApiResponse<ClassDetailRes> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        return ApiResponse.success(eduClassService.getClassById(id));
    }

    /**
     * 分页查询班级
     * @param req 分页查询参数
     * @return 分页班级列表
     */
    @GetMapping
    @Operation(description = "分页查询班级")
    @RequirePermission("class:view")
    public ApiResponse<PageResult<ClassPageRes>> pageClass(@Valid @ModelAttribute ClassPageReq req) {
        return ApiResponse.success(eduClassService.pageClass(req));
    }

    /**
     * 生成班级课表
     * @param id 班级ID
     * @param req 开班时间、授课老师ID
     * @return 生成的课表记录数
     */
    @PostMapping("/{id}/generateSchedule")
    @Operation(description = "生成班级课表")
    @RequirePermission("class:update")
    public ApiResponse<ScheduleGenerateRes> generateSchedule(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Valid @RequestBody ScheduleGenerateReq req) {
        return ApiResponse.success(eduClassService.generateSchedule(id, req));
    }

    /**
     * 分页查询班级课表
     * @param req 分页查询参数
     * @return 分页课表列表
     */
    @GetMapping("/schedule")
    @Operation(description = "分页查询班级课表")
    @RequirePermission("class:view")
    public ApiResponse<PageResult<SchedulePageRes>> pageSchedule(@Valid @ModelAttribute SchedulePageReq req) {
        return ApiResponse.success(eduClassService.pageSchedule(req));
    }

    /**
     * 查询指定日期范围内已排课的老师ID
     */
    @GetMapping("/{id}/busyTeachers")
    @Operation(description = "查询已排课老师")
    @RequirePermission("class:view")
    public ApiResponse<List<Long>> getBusyTeachers(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ApiResponse.success(eduClassService.getBusyTeacherIds(id, startDate, endDate));
    }

    /**
     * 更新课程（老师+阶段）
     */
    @PutMapping("/schedule/{id}")
    @Operation(description = "更新课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> updateSchedule(
            @Parameter(description = "课表ID") @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> params) {
        Long teacherId = params.get("teacherId") != null ? Long.valueOf(params.get("teacherId").toString()) : null;
        String stageName = params.get("stageName") != null ? params.get("stageName").toString() : null;
        eduClassService.updateSchedule(id, teacherId, stageName);
        return ApiResponse.success();
    }

    /**
     * 删除单条课程
     */
    @DeleteMapping("/schedule/{id}")
    @Operation(description = "删除课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> deleteSchedule(
            @Parameter(description = "课表ID") @PathVariable Long id) {
        eduClassService.deleteSchedule(id);
        return ApiResponse.success();
    }

    /**
     * 查询指定日期已排课的老师ID（用于修改老师时筛选空闲教师）
     */
    @GetMapping("/schedule/busyTeachersByDate")
    @Operation(description = "查询指定日期已排课老师")
    @RequirePermission("class:view")
    public ApiResponse<List<Long>> getBusyTeachersByDate(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        return ApiResponse.success(eduClassService.getBusyTeacherIdsByDate(scheduleDate));
    }

    /**
     * 在指定日期插入新课程
     */
    @PostMapping("/schedule/insert")
    @Operation(description = "插入课程")
    @RequirePermission("class:update")
    public ApiResponse<Void> insertSchedule(
            @RequestBody EduClassScheduleEntity entity,
            @Parameter(description = "插入日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        eduClassService.insertSchedule(entity, scheduleDate);
        return ApiResponse.success();
    }
}
