package cn.yanque.models.interview.mapper;

import cn.yanque.models.interview.pojo.InterviewReviewDtos;
import cn.yanque.models.interview.pojo.InterviewReviewRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InterviewReviewRecordMapper {
    int insert(InterviewReviewRecordEntity entity);

    InterviewReviewRecordEntity selectById(@Param("id") Long id);

    List<InterviewReviewRecordEntity> selectPage(InterviewReviewDtos.PageReq req);

    List<InterviewReviewRecordEntity> selectTranscribing(@Param("limit") Integer limit);

    int markTranscribing(@Param("id") Long id, @Param("taskId") String taskId, @Param("now") Date now);

    int markAnalyzing(@Param("id") Long id, @Param("dialogueJson") String dialogueJson, @Param("now") Date now);

    int markDone(@Param("id") Long id, @Param("dialogueJson") String dialogueJson,
                 @Param("reportJson") String reportJson, @Param("now") Date now);

    int markFailed(@Param("id") Long id, @Param("errorMessage") String errorMessage, @Param("now") Date now);

    int markQuestionProcessing(@Param("id") Long id, @Param("now") Date now);

    int markQuestionDone(@Param("id") Long id, @Param("extracted") Integer extracted,
                         @Param("created") Integer created, @Param("merged") Integer merged,
                         @Param("now") Date now);

    int markQuestionFailed(@Param("id") Long id, @Param("reason") String reason, @Param("now") Date now);
}
