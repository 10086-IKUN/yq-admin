package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentFront.biz.StudentScoreBiz;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreOverviewRes;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
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
 * 学员端成绩控制器。
 *
 * <p>学员端只能查看自己的成绩，不能由前端传学员ID。
 * 因此前端请求到这里以后，后端统一从登录令牌中解析 studentId 和 studentNo。</p>
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
     * 获取成绩明细列表。
     *
     * <p>明细列表会合并已批改作业和已出分考试。
     * 这个接口保留给需要明细表格的页面使用。</p>
     *
     * @param request 当前登录请求
     * @return 当前学员的成绩明细列表
     */
    @GetMapping
    @Operation(description = "获取成绩明细列表")
    public ApiResponse<List<StudentScoreRes>> list(HttpServletRequest request) {
        return ApiResponse.success(studentScoreBiz.list(
                StudentAuthUtil.getStudentId(request),
                StudentAuthUtil.getStudentNo(request)
        ));
    }

    /**
     * 获取课程综合成绩总览。
     *
     * <p>当前规则是：作业平均分 * 40% + 考试平均分 * 60%。
     * 如果某一类暂时没有成绩，该类平均分按 0 参与计算。</p>
     *
     * @param request 当前登录请求
     * @return 当前学员的课程综合成绩、作业成绩和考试成绩
     */
    @GetMapping("/overview")
    @Operation(description = "获取课程综合成绩总览")
    public ApiResponse<StudentScoreOverviewRes> overview(HttpServletRequest request) {
        return ApiResponse.success(studentScoreBiz.overview(
                StudentAuthUtil.getStudentId(request),
                StudentAuthUtil.getStudentNo(request)
        ));
    }
}
