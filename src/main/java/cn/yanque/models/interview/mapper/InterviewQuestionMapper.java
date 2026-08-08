package cn.yanque.models.interview.mapper;

import cn.yanque.models.interview.pojo.InterviewQuestionBankEntity;
import cn.yanque.models.interview.pojo.InterviewQuestionDtos;
import cn.yanque.models.interview.pojo.InterviewQuestionSourceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InterviewQuestionMapper {
    InterviewQuestionBankEntity selectById(@Param("id") Long id);
    InterviewQuestionBankEntity selectByNormalizedQuestion(@Param("question") String question);
    List<InterviewQuestionBankEntity> selectPage(InterviewQuestionDtos.PageReq req);
    int insertQuestion(InterviewQuestionBankEntity entity);
    int insertSource(InterviewQuestionSourceEntity entity);
    int incrementSource(@Param("id") Long id, @Param("reviewId") Long reviewId, @Param("now") Date now);
    int markVector(@Param("id") Long id, @Param("status") String status, @Param("vectorId") String vectorId);
    int updateAuditStatus(@Param("id") Long id, @Param("status") String status);
    List<InterviewQuestionSourceEntity> selectSources(@Param("questionId") Long questionId);
    List<InterviewQuestionBankEntity> selectPublishedCandidates(@Param("limit") int limit);
}
