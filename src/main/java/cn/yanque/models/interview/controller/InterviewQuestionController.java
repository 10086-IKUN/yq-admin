package cn.yanque.models.interview.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.interview.pojo.InterviewQuestionDtos;
import cn.yanque.models.interview.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview-questions")
public class InterviewQuestionController {
    @Autowired private InterviewQuestionService service;

    @GetMapping
    @RequirePermission("interview-question:view")
    public ApiResponse<PageResult<InterviewQuestionDtos.Item>> page(@ModelAttribute InterviewQuestionDtos.PageReq req) {
        return ApiResponse.success(service.page(req));
    }

    @GetMapping("/{id}")
    @RequirePermission("interview-question:view")
    public ApiResponse<InterviewQuestionDtos.Detail> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @PostMapping("/{id}/audit")
    @RequirePermission("interview-question:manage")
    public ApiResponse<Void> audit(@PathVariable Long id, @Valid @RequestBody InterviewQuestionDtos.AuditReq req) {
        service.audit(id, req.getAuditStatus()); return ApiResponse.success();
    }

    @PostMapping("/reviews/{reviewId}/process")
    @RequirePermission("interview-question:manage")
    public ApiResponse<Void> process(@PathVariable Long reviewId) {
        service.processReview(reviewId); return ApiResponse.success();
    }

}
