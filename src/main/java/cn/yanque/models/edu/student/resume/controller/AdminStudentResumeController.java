package cn.yanque.models.edu.student.resume.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.student.resume.pojo.StudentResumeDtos;
import cn.yanque.models.edu.student.resume.service.StudentResumeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/{studentId}/resume")
public class AdminStudentResumeController {
    @Autowired
    private StudentResumeService resumeService;

    @GetMapping
    @RequirePermission("student:view")
    public ApiResponse<StudentResumeDtos.Info> get(@PathVariable Long studentId) {
        return ApiResponse.success(resumeService.get(studentId));
    }

    @PutMapping
    @RequirePermission("student:update")
    public ApiResponse<StudentResumeDtos.Info> save(@PathVariable Long studentId,
                                                    @Valid @RequestBody StudentResumeDtos.SaveReq req) {
        return ApiResponse.success(resumeService.save(studentId, req));
    }

    @PostMapping("/retry")
    @RequirePermission("student:update")
    public ApiResponse<StudentResumeDtos.Info> retry(@PathVariable Long studentId) {
        return ApiResponse.success(resumeService.retry(studentId));
    }

    @GetMapping("/preview-url")
    @RequirePermission("student:view")
    public ApiResponse<String> previewUrl(@PathVariable Long studentId) {
        return ApiResponse.success("success", resumeService.previewUrl(studentId));
    }

    @GetMapping("/download-url")
    @RequirePermission("student:view")
    public ApiResponse<String> downloadUrl(@PathVariable Long studentId) {
        return ApiResponse.success("success", resumeService.downloadUrl(studentId));
    }
}
