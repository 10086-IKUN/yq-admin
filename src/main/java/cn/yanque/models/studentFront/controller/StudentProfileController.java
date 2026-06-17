package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.biz.StudentProfileBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学员端个人中心控制器
 * 提供学员查看、修改个人信息接口
 */
@RestController
@RequestMapping("/api/student/profile")
@Slf4j
@SkipPermission
@Tag(name = "StudentProfileController", description = "学员端个人中心")
public class StudentProfileController {

    @Autowired
    private StudentProfileBiz studentProfileBiz;

    /**
     * 获取个人信息
     * @return 学员信息
     */
    @GetMapping
    @Operation(description = "获取个人信息")
    public ApiResponse<EduStudentEntity> getProfile() {
        return ApiResponse.success(studentProfileBiz.getProfile());
    }

    /**
     * 修改个人信息
     * @param entity 学员信息
     * @return 修改结果
     */
    @PutMapping
    @Operation(description = "修改个人信息")
    public ApiResponse<Void> updateProfile(@RequestBody EduStudentEntity entity) {
        studentProfileBiz.updateProfile(entity);
        return ApiResponse.success();
    }
}
