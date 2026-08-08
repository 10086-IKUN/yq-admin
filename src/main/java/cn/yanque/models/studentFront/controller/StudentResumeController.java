package cn.yanque.models.studentFront.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.student.resume.pojo.StudentResumeDtos;
import cn.yanque.models.edu.student.resume.service.StudentResumeService;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/resume")
@SkipPermission
public class StudentResumeController {
    @Autowired
    private StudentResumeService resumeService;

    @GetMapping
    public ApiResponse<StudentResumeDtos.Info> get(HttpServletRequest request) {
        return ApiResponse.success(resumeService.get(StudentAuthUtil.getStudentId(request)));
    }

    @PutMapping
    public ApiResponse<StudentResumeDtos.Info> save(@Valid @RequestBody StudentResumeDtos.SaveReq req,
                                                    HttpServletRequest request) {
        return ApiResponse.success(resumeService.save(StudentAuthUtil.getStudentId(request), req));
    }

    @PostMapping("/retry")
    public ApiResponse<StudentResumeDtos.Info> retry(HttpServletRequest request) {
        return ApiResponse.success(resumeService.retry(StudentAuthUtil.getStudentId(request)));
    }

    @GetMapping("/preview-url")
    public ApiResponse<String> previewUrl(HttpServletRequest request) {
        return ApiResponse.success("success", resumeService.previewUrl(StudentAuthUtil.getStudentId(request)));
    }

    @GetMapping("/download-url")
    public ApiResponse<String> downloadUrl(HttpServletRequest request) {
        return ApiResponse.success("success", resumeService.downloadUrl(StudentAuthUtil.getStudentId(request)));
    }
}
