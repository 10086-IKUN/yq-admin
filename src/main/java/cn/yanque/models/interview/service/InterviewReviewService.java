package cn.yanque.models.interview.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.interview.pojo.InterviewReviewDtos;

public interface InterviewReviewService {
    PageResult<InterviewReviewDtos.Item> page(InterviewReviewDtos.PageReq req);
    InterviewReviewDtos.Item create(Long operatorId, InterviewReviewDtos.CreateReq req, boolean autoTranscribe);
    InterviewReviewDtos.Item detail(Long id);
    void retry(Long id, Long studentId);
    void pollTranscribing();
}
