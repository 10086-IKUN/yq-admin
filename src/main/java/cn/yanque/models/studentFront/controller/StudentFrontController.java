package cn.yanque.models.studentFront.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentFront.biz.StudentFrontBiz;
import cn.yanque.models.studentFront.pojo.vo.req.StudentLoginReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentLoginRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@Slf4j
@Tag(name = "StudentFrontController", description = "学生端")

/**
 * 学生端登录接口。
 *
 * <p>登录成功后返回学生 JWT、签名密钥、学生信息和可访问权限。</p>
 */
public class StudentFrontController {

    @Autowired
    private StudentFrontBiz studentFrontBiz;

    @PostMapping("/login")
    @Operation(description = "学生登录")
    public ApiResponse<StudentLoginRes> login(@Valid @RequestBody StudentLoginReq req) {
        return ApiResponse.success(studentFrontBiz.login(req));
    }
}
