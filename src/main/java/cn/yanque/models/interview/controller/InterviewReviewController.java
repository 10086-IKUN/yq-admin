package cn.yanque.models.interview.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.interview.pojo.InterviewReviewDtos;
import cn.yanque.models.interview.service.InterviewReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview-reviews")
public class InterviewReviewController {
    @Autowired
    private InterviewReviewService service;

    @GetMapping
    @RequirePermission("interview-review:view")
    public ApiResponse<PageResult<InterviewReviewDtos.Item>> page(@ModelAttribute InterviewReviewDtos.PageReq req) {
        return ApiResponse.success(service.page(req));
    }

    @GetMapping("/{id}")
    @RequirePermission("interview-review:view")
    public ApiResponse<InterviewReviewDtos.Item> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @PostMapping
    @RequirePermission("interview-review:manage")
    public ApiResponse<InterviewReviewDtos.Item> create(@Valid @RequestBody InterviewReviewDtos.CreateReq req,
                                                        HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        Long operatorId = userId == null ? null : Long.valueOf(String.valueOf(userId));
        return ApiResponse.success(service.create(operatorId, req, true));
    }

    @PostMapping("/{id}/retry")
    @RequirePermission("interview-review:manage")
    public ApiResponse<Void> retry(@PathVariable Long id) {
        service.retry(id, null);
        return ApiResponse.success();
    }
}
