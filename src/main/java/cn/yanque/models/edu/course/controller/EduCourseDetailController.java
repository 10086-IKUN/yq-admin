package cn.yanque.models.edu.course.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.edu.course.pojo.vo.req.CourseDetailCreateReq;
import cn.yanque.models.edu.course.pojo.vo.req.CourseDetailPageReq;
import cn.yanque.models.edu.course.pojo.vo.req.CourseDetailUpdateReq;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailCreateRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailDeleteRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailDetailRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailPageRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailUpdateRes;
import cn.yanque.models.edu.course.service.EduCourseDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程详情Controller
 *
 * RESTful API设计：
 * - GET    /api/eduCourseDetail      -> 分页列表
 * - GET    /api/eduCourseDetail/{id} -> 详情
 * - POST   /api/eduCourseDetail      -> 新增
 * - PUT    /api/eduCourseDetail/{id} -> 更新
 * - DELETE /api/eduCourseDetail/{id} -> 删除
 *
 * 注解说明：
 * @RestController: 标记为Controller，返回值自动序列化为JSON
 * @RequestMapping: 定义基础路径
 * @Tag: Swagger文档分组
 * @RequirePermission: 自定义权限校验注解
 */
@Tag(name = "课程详情管理")
@RestController
@RequestMapping("/api/eduCourseDetail")
public class EduCourseDetailController {

    /** 自动注入Service */
    @Autowired
    private EduCourseDetailService eduCourseDetailService;

    /**
     * 新增课程详情
     *
     * @RequestBody: 从请求体读取JSON并反序列化为Java对象
     * @Valid: 触发JSR 380参数校验，校验失败抛出MethodArgumentNotValidException
     * @RequirePermission("course:*"): 需要课程模块的所有权限
     */
    @Operation(summary = "新增课程详情")
    @RequirePermission("course:*")
    @PostMapping
    public ApiResponse<CourseDetailCreateRes> add(@RequestBody @Valid CourseDetailCreateReq req) {
        return ApiResponse.success(eduCourseDetailService.addCourseDetail(req));
    }

    /**
     * 更新课程详情
     *
     * @PathVariable: 从URL路径中提取参数（如/api/eduCourseDetail/123中的123）
     * 注意：路径参数id和请求体中的id保持一致
     */
    @Operation(summary = "更新课程详情")
    @RequirePermission("course:*")
    @PutMapping("/{id}")
    public ApiResponse<CourseDetailUpdateRes> update(@PathVariable Long id, @RequestBody @Valid CourseDetailUpdateReq req) {
        req.setId(id);  // 将路径参数设置到请求对象中
        return ApiResponse.success(eduCourseDetailService.updateCourseDetail(req));
    }

    /**
     * 删除课程详情
     * @PathVariable: 从URL路径中提取要删除的记录ID
     */
    @Operation(summary = "删除课程详情")
    @RequirePermission("course:*")
    @DeleteMapping("/{id}")
    public ApiResponse<CourseDetailDeleteRes> delete(@PathVariable Long id) {
        return ApiResponse.success(eduCourseDetailService.deleteCourseDetail(id));
    }

    /**
     * 查询课程详情
     * @PathVariable: 从URL路径中提取要查询的记录ID
     */
    @Operation(summary = "课程详情详情")
    @RequirePermission("course:*")
    @GetMapping("/{id}")
    public ApiResponse<CourseDetailDetailRes> detail(@PathVariable Long id) {
        return ApiResponse.success(eduCourseDetailService.getCourseDetailById(id));
    }

    /**
     * 分页查询课程详情列表
     *
     * @ModelAttribute: 将查询参数（?courseId=1&pageNum=1）绑定到Java对象
     * 与@RequestBody的区别：
     * - @ModelAttribute: 用于表单参数或URL查询参数
     * - @RequestBody: 用于JSON请求体
     */
    @Operation(summary = "课程详情分页列表")
    @RequirePermission("course:*")
    @GetMapping
    public ApiResponse<PageResult<CourseDetailPageRes>> page(@ModelAttribute CourseDetailPageReq req) {
        return ApiResponse.success(eduCourseDetailService.pageCourseDetail(req));
    }

    /**
     * 导入并解析excel表格
     */
    @Operation(summary = "导入并解析excel表格")
    @RequirePermission("course:*")
    @PostMapping("/import/{courseId}")
    public ApiResponse importExcel(@PathVariable Long courseId, @RequestParam MultipartFile file) {
        eduCourseDetailService.importExcel(courseId, file);
        return ApiResponse.success();
    }

    /**
     * 获取课程的所有阶段名称
     */
    @Operation(summary = "获取课程的阶段列表")
    @RequirePermission("course:*")
    @GetMapping("/stages/{courseId}")
    public ApiResponse<List<String>> getStageNames(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        return ApiResponse.success(eduCourseDetailService.getStageNames(courseId));
    }
}
