package cn.yanque.models.studentVisit.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentVisit.pojo.entity.StudentVisitEntity;
import cn.yanque.models.studentVisit.service.StudentVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-visit")
@Tag(name = "StudentVisitController", description = "学员回访管理")
public class StudentVisitController {

    @Autowired
    private StudentVisitService studentVisitService;

    @GetMapping("/today")
    @RequirePermission("student-visit:list")
    @Operation(description = "获取今日需回访的学员")
    public ApiResponse<List<StudentVisitEntity>> today(HttpServletRequest request) {
        Long userId = currentUserId(request);
        return ApiResponse.success(studentVisitService.getTodayVisitList(userId));
    }

    @GetMapping("/history/{studentId}")
    @RequirePermission("student-visit:list")
    @Operation(description = "查询学员回访历史")
    public ApiResponse<List<StudentVisitEntity>> history(@PathVariable Long studentId) {
        return ApiResponse.success(studentVisitService.getHistory(studentId));
    }

    @PostMapping
    @RequirePermission("student-visit:submit")
    @Operation(description = "提交回访记录")
    public ApiResponse<Void> submit(@RequestBody StudentVisitEntity entity, HttpServletRequest request) {
        Long userId = currentUserId(request);
        entity.setTeacherId(userId);
        studentVisitService.submitVisit(entity);
        return ApiResponse.success(null);
    }

    private Long currentUserId(HttpServletRequest request) {
        return Long.parseLong(String.valueOf(request.getAttribute("userId")));
    }
}
