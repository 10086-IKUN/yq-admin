package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.biz.StudentScheduleBiz;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学员端课表控制器。
 *
 * <p>只提供当前登录学员自己的课表接口。当前学员身份来自登录 token，
 * 不能让前端传 classId，否则学生可能查看到其他班级课表。</p>
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
     * 获取当前登录学员的个人课表。
     *
     * <p>这里先从 request 中取出学生 ID，再交给业务层按学生绑定班级查询课表。</p>
     *
     * @param request HTTP 请求，拦截器会把当前学员ID放入 request attribute
     * @return 当前学员所在班级的课表列表
     */
    @GetMapping
    @Operation(description = "获取个人课表")
    public ApiResponse<List<SchedulePageRes>> list(HttpServletRequest request) {
        Long studentId = StudentAuthUtil.getStudentId(request);
        return ApiResponse.success(studentScheduleBiz.list(studentId));
    }
}
