package cn.yanque.models.interview.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.interview.pojo.InterviewQuestionDtos;
import cn.yanque.models.interview.pojo.InterviewQuestionBankEntity;

import java.util.List;

public interface InterviewQuestionService {
    void processReview(Long reviewId);
    PageResult<InterviewQuestionDtos.Item> page(InterviewQuestionDtos.PageReq req);
    InterviewQuestionDtos.Detail detail(Long id);
    void audit(Long id, String status);

    List<InterviewQuestionBankEntity> recommendPublishedForMockInterview(String profileQuery, int limit);
}
