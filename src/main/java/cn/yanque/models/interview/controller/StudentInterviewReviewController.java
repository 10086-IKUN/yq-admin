package cn.yanque.models.interview.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.interview.pojo.InterviewReviewDtos;
import cn.yanque.models.interview.service.InterviewReviewService;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/interview-reviews")
@SkipPermission
public class StudentInterviewReviewController {
    @Autowired
    private InterviewReviewService service;

    @GetMapping
    public ApiResponse<PageResult<InterviewReviewDtos.Item>> page(@ModelAttribute InterviewReviewDtos.PageReq req,
                                                                  HttpServletRequest request) {
        req.setStudentId(StudentAuthUtil.getStudentId(request));
        return ApiResponse.success(service.page(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<InterviewReviewDtos.Item> detail(@PathVariable Long id, HttpServletRequest request) {
        InterviewReviewDtos.Item item = service.detail(id);
        if (!StudentAuthUtil.getStudentId(request).equals(item.getStudentId())) {
            throw new cn.yanque.common.exception.BusinessException(403, "只能查看自己的面试复盘");
        }
        return ApiResponse.success(item);
    }

    @PostMapping
    public ApiResponse<InterviewReviewDtos.Item> create(@Valid @RequestBody InterviewReviewDtos.CreateReq req,
                                                        HttpServletRequest request) {
        req.setStudentId(StudentAuthUtil.getStudentId(request));
        return ApiResponse.success(service.create(null, req, true));
    }

    @PostMapping("/{id}/retry")
    public ApiResponse<Void> retry(@PathVariable Long id, HttpServletRequest request) {
        service.retry(id, StudentAuthUtil.getStudentId(request));
        return ApiResponse.success();
    }
}
