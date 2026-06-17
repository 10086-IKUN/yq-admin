package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.biz.StudentScheduleBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学员端课表控制器
 * 提供学员查看个人课表接口
 */
@RestController
@RequestMapping("/api/student/schedule")
@Slf4j
@SkipPermission
@Tag(name = "StudentScheduleController", description = "学员端课表管理")
public class StudentScheduleController {

    @Autowired
    private StudentScheduleBiz studentScheduleBiz;

    /**
     * 获取个人课表
     * @return 课表列表
     */
    @GetMapping
    @Operation(description = "获取个人课表")
    public ApiResponse<List<SchedulePageRes>> list() {
        return ApiResponse.success(studentScheduleBiz.list());
    }
}
