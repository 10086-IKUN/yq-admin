package cn.yanque.models.duty.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentCreateReq;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentPageReq;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentUpdateReq;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentCreateRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentDeleteRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentDetailRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentPageRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentUpdateRes;
import cn.yanque.models.duty.service.EduDutyAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 值班安排Controller
 */
@Tag(name = "值班安排管理")
@RestController
@RequestMapping("/api/eduDutyAssignment")
public class EduDutyAssignmentController {

    @Autowired
    private EduDutyAssignmentService eduDutyAssignmentService;

    @Operation(summary = "新增值班安排")
    @RequirePermission("duty:*")
    @PostMapping
    public ApiResponse<DutyAssignmentCreateRes> add(@RequestBody @Valid DutyAssignmentCreateReq req) {
        return ApiResponse.success(eduDutyAssignmentService.addDutyAssignment(req));
    }

    @Operation(summary = "更新值班安排")
    @RequirePermission("duty:*")
    @PutMapping("/{id}")
    public ApiResponse<DutyAssignmentUpdateRes> update(@PathVariable Long id, @RequestBody @Valid DutyAssignmentUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduDutyAssignmentService.updateDutyAssignment(req));
    }

    @Operation(summary = "删除值班安排")
    @RequirePermission("duty:*")
    @DeleteMapping("/{id}")
    public ApiResponse<DutyAssignmentDeleteRes> delete(@PathVariable Long id) {
        return ApiResponse.success(eduDutyAssignmentService.deleteDutyAssignment(id));
    }

    @Operation(summary = "值班安排详情")
    @RequirePermission("duty:*")
    @GetMapping("/{id}")
    public ApiResponse<DutyAssignmentDetailRes> detail(@PathVariable Long id) {
        return ApiResponse.success(eduDutyAssignmentService.getDutyAssignmentById(id));
    }

    @Operation(summary = "值班安排分页列表")
    @RequirePermission("duty:*")
    @GetMapping
    public ApiResponse<PageResult<DutyAssignmentPageRes>> page(@ModelAttribute DutyAssignmentPageReq req) {
        return ApiResponse.success(eduDutyAssignmentService.pageDutyAssignment(req));
    }

    @Operation(summary = "查询指定日期指定类型已占用的老师ID")
    @RequirePermission("duty:*")
    @GetMapping("/busyTeachers")
    public ApiResponse<List<Long>> getBusyTeacherIds(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dutyDate,
            @RequestParam String dutyType) {
        return ApiResponse.success(eduDutyAssignmentService.getBusyTeacherIds(dutyDate, dutyType));
    }
}
