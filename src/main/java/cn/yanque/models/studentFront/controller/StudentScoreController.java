package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentFront.biz.StudentScoreBiz;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学员端成绩控制器
 * 提供学员查看成绩接口
 */
@RestController
@RequestMapping("/api/student/score")
@Slf4j
@SkipPermission
@Tag(name = "StudentScoreController", description = "学员端成绩管理")
public class StudentScoreController {

    @Autowired
    private StudentScoreBiz studentScoreBiz;

    /**
     * 获取成绩列表
     * @return 成绩列表
     */
    @GetMapping
    @Operation(description = "获取成绩列表")
    public ApiResponse<List<StudentScoreRes>> list() {
        return ApiResponse.success(studentScoreBiz.list());
    }
}
