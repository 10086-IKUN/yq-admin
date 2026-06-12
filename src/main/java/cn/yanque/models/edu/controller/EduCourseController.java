package cn.yanque.models.edu.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.CourseCreateReq;
import cn.yanque.common.pojo.vo.req.CoursePageReq;
import cn.yanque.common.pojo.vo.req.CourseUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailRes;
import cn.yanque.common.pojo.vo.res.CoursePageRes;
import cn.yanque.common.pojo.vo.res.CourseUpdateRes;
import cn.yanque.models.edu.service.EduCourseService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程管理控制器
 * 提供课程的增删改查接口
 */
@RestController
@RequestMapping("/api/eduCourse")
@Slf4j
@Tag(name = "EduCourseController", description = "课程管理")
public class EduCourseController {

    @Autowired
    private EduCourseService eduCourseService;

    /**
     * 添加课程
     * @param req 创建课程请求参数
     * @return 创建成功的课程信息
     */
    @PostMapping
    @Operation(description = "添加课程")
    @RequirePermission("course:add")
    public ApiResponse<CourseCreateRes> addCourse(@Valid @RequestBody CourseCreateReq req) {
        return ApiResponse.success(eduCourseService.addCourse(req));
    }

    /**
     * 修改课程
     * @param id 课程ID
     * @param req 更新课程请求参数
     * @return 更新后的课程信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改课程")
    @RequirePermission("course:update")
    public ApiResponse<CourseUpdateRes> updateCourse(@Parameter(description = "课程ID") @PathVariable Long id,
                                                     @Valid @RequestBody CourseUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduCourseService.updateCourse(req));
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除课程")
    @RequirePermission("course:delete")
    public ApiResponse<CourseDeleteRes> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        return ApiResponse.success(eduCourseService.deleteCourse(id));
    }

    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询课程")
    @RequirePermission("course:view")
    public ApiResponse<CourseDetailRes> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        return ApiResponse.success(eduCourseService.getCourseById(id));
    }

    /**
     * 分页查询课程
     * @param req 分页查询参数
     * @return 分页课程列表
     */
    @GetMapping
    @Operation(description = "分页查询课程")
    @RequirePermission("course:view")
    public ApiResponse<PageResult<CoursePageRes>> pageCourse(@Valid @ModelAttribute CoursePageReq req) {
        return ApiResponse.success(eduCourseService.pageCourse(req));
    }
}
