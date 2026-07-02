package cn.yanque.models.studentTag.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentTag.pojo.vo.StudentTagVO;
import cn.yanque.models.studentTag.service.StudentTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-tag")
@Tag(name = "StudentTagController", description = "学员标签管理")

/**
 * 学生标签接口。
 *
 * <p>用于查看、确认和手动重新计算学生标签，标签会影响后续回访节奏。</p>
 */
public class StudentTagController {

    @Autowired
    private StudentTagService studentTagService;

    @GetMapping("/list")
    @RequirePermission("student-tag:list")
    @Operation(description = "查询学员标签列表")
    public ApiResponse<List<StudentTagVO>> list(
            @RequestParam(required = false) String tagType,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(studentTagService.list(tagType, keyword));
    }

    @PutMapping("/{id}/confirm")
    @RequirePermission("student-tag:confirm")
    @Operation(description = "班主任确认标签")
    public ApiResponse<Void> confirm(@PathVariable Long id,
                                     @RequestParam(required = false) String tagType,
                                     HttpServletRequest request) {
        Long userId = currentUserId(request);
        studentTagService.confirm(id, userId, tagType);
        return ApiResponse.success(null);
    }

    @PostMapping("/calculate")
    @RequirePermission("student-tag:calculate")
    @Operation(description = "手动触发标签计算")
    public ApiResponse<Void> calculate() {
        studentTagService.calculateAllTags();
        return ApiResponse.success(null);
    }

    @GetMapping("/my-class")
    @RequirePermission("student-tag:list")
    @Operation(description = "获取当前教师班级的学员标签")
    public ApiResponse<List<StudentTagVO>> myClass(HttpServletRequest request) {
        Long userId = currentUserId(request);
        return ApiResponse.success(studentTagService.listByTeacherId(userId));
    }

    private Long currentUserId(HttpServletRequest request) {
        return Long.parseLong(String.valueOf(request.getAttribute("userId")));
    }
}
