package cn.yanque.models.exam.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamAnswerSaveReq;
import cn.yanque.models.exam.pojo.vo.res.StudentExamDetailRes;
import cn.yanque.models.exam.service.StudentExamService;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学员端在线考试接口。
 */
@RestController
@RequestMapping("/api/student/exams")
@SkipPermission
public class StudentExamController {

    @Autowired
    private StudentExamService studentExamService;

    /** 查询当前学员所在班级的考试列表。 */
    @GetMapping
    public ApiResponse<List<ExamScheduleEntity>> list(HttpServletRequest request) {
        return ApiResponse.success(studentExamService.list(StudentAuthUtil.getStudentId(request)));
    }

    /** 查询考试详情和当前考试记录。 */
    @GetMapping("/{id}")
    public ApiResponse<StudentExamDetailRes> detail(@PathVariable Long id,
                                                    HttpServletRequest request) {
        return ApiResponse.success(studentExamService.detail(id, StudentAuthUtil.getStudentId(request)));
    }

    /** 首次进入考试，创建唯一考试记录。 */
    @PostMapping("/{id}/start")
    public ApiResponse<StudentExamDetailRes> start(@PathVariable Long id,
                                                   HttpServletRequest request) {
        return ApiResponse.success(studentExamService.start(id, StudentAuthUtil.getStudentId(request)));
    }

    /** 批量保存当前页面答案。 */
    @PutMapping("/attempts/{id}/answers")
    public ApiResponse<Void> saveAnswers(@PathVariable Long id,
                                         @Valid @RequestBody ExamAnswerSaveReq req,
                                         HttpServletRequest request) {
        studentExamService.saveAnswers(id, req, StudentAuthUtil.getStudentId(request));
        return ApiResponse.success();
    }

    /** 最终交卷，成功后不允许再次作答。 */
    @PostMapping("/attempts/{id}/submit")
    public ApiResponse<StudentExamDetailRes> submit(@PathVariable Long id,
                                                    HttpServletRequest request) {
        return ApiResponse.success(studentExamService.submit(id, StudentAuthUtil.getStudentId(request)));
    }
}
