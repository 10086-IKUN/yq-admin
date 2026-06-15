package cn.yanque.models.edu.student.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.edu.student.pojo.vo.req.StudentCreateReq;
import cn.yanque.models.edu.student.pojo.vo.req.StudentPageReq;
import cn.yanque.models.edu.student.pojo.vo.req.StudentUpdateReq;
import cn.yanque.models.edu.student.pojo.vo.res.StudentCreateRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentDeleteRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentDetailRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentPageRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentUpdateRes;
import cn.yanque.models.edu.student.service.EduStudentService;
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
 * 学员管理控制器
 * 提供学员的增删改查接口
 */
@RestController
@RequestMapping("/api/eduStudent")
@Slf4j
@Tag(name = "EduStudentController", description = "学员管理")
public class EduStudentController {

    @Autowired
    private EduStudentService eduStudentService;

    /**
     * 添加学员
     * @param req 创建学员请求参数
     * @return 创建成功的学员信息
     */
    @PostMapping
    @Operation(description = "添加学员")
    @RequirePermission("student:add")
    public ApiResponse<StudentCreateRes> addStudent(@Valid @RequestBody StudentCreateReq req) {
        return ApiResponse.success(eduStudentService.addStudent(req));
    }

    /**
     * 修改学员
     * @param id 学员ID
     * @param req 更新学员请求参数
     * @return 更新后的学员信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改学员")
    @RequirePermission("student:update")
    public ApiResponse<StudentUpdateRes> updateStudent(@Parameter(description = "学员ID") @PathVariable Long id,
                                                       @Valid @RequestBody StudentUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduStudentService.updateStudent(req));
    }

    /**
     * 删除学员
     * @param id 学员ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除学员")
    @RequirePermission("student:delete")
    public ApiResponse<StudentDeleteRes> deleteStudent(@Parameter(description = "学员ID") @PathVariable Long id) {
        return ApiResponse.success(eduStudentService.deleteStudent(id));
    }

    /**
     * 根据ID查询学员
     * @param id 学员ID
     * @return 学员详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询学员")
    @RequirePermission("student:view")
    public ApiResponse<StudentDetailRes> getStudentById(@Parameter(description = "学员ID") @PathVariable Long id) {
        return ApiResponse.success(eduStudentService.getStudentById(id));
    }

    /**
     * 分页查询学员
     * @param req 分页查询参数
     * @return 分页学员列表
     */
    @GetMapping
    @Operation(description = "分页查询学员")
    @RequirePermission("student:view")
    public ApiResponse<PageResult<StudentPageRes>> pageStudent(@Valid @ModelAttribute StudentPageReq req) {
        return ApiResponse.success(eduStudentService.pageStudent(req));
    }
}
