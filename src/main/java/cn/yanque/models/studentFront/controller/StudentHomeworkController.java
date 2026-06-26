package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkSubmissionReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.studentFront.biz.StudentHomeworkBiz;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学员端作业控制器
 * 提供学员查看作业、提交作业接口
 */
@RestController
@RequestMapping("/api/student/homework")
@Slf4j
@SkipPermission
@Tag(name = "StudentHomeworkController", description = "学员端作业管理")
public class StudentHomeworkController {

    @Autowired
    private StudentHomeworkBiz studentHomeworkBiz;

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    @GetMapping
    @Operation(description = "获取作业列表")
    public ApiResponse<PageResult<HomeworkAssignmentRes>> list(@Valid @ModelAttribute HomeworkAssignmentPageReq req,
                                                               HttpServletRequest request) {
        // 强制使用学生所在班级ID，防止查看其他班级作业
        Long classId = StudentAuthUtil.getClassId(request);
        req.setClassId(classId);

        String studentNo = StudentAuthUtil.getStudentNo(request);
        return ApiResponse.success(studentHomeworkBiz.list(req, studentNo));
    }

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业详情
     */
    @GetMapping("/{id}")
    @Operation(description = "获取作业详情")
    public ApiResponse<HomeworkAssignmentRes> detail(@PathVariable Long id) {
        return ApiResponse.success(studentHomeworkBiz.detail(id));
    }

    /**
     * 提交作业
     * @param req 提交请求
     * @param request HTTP请求
     * @return 提交记录
     */
    @PostMapping("/submit")
    @Operation(description = "提交作业")
    public ApiResponse<HomeworkSubmissionEntity> submit(@Valid @RequestBody HomeworkSubmissionReq req, HttpServletRequest request) {
        String studentNo = StudentAuthUtil.getStudentNo(request);
        String studentName = StudentAuthUtil.getStudentName(request);
        return ApiResponse.success(studentHomeworkBiz.submit(req, studentNo, studentName));
    }

    /**
     * 获取学员的作业提交记录
     * @param assignmentId 作业ID
     * @param request HTTP请求
     * @return 提交记录
     */
    @GetMapping("/submission/{assignmentId}")
    @Operation(description = "获取作业提交记录")
    public ApiResponse<HomeworkSubmissionEntity> getSubmission(@PathVariable Long assignmentId, HttpServletRequest request) {
        String studentNo = StudentAuthUtil.getStudentNo(request);
        return ApiResponse.success(studentHomeworkBiz.getSubmission(assignmentId, studentNo));
    }
}
