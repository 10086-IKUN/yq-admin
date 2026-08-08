package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlRunQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlRunMapper {

    void insert(TextToSqlRunEntity entity);

    int updateByConversationId(TextToSqlRunEntity entity);

    int updateStatusByConversationId(@Param("conversationId") String conversationId, @Param("status") String status);

    int updateFeedbackById(@Param("id") Long id,
                           @Param("feedbackResult") String feedbackResult,
                           @Param("feedbackErrorType") String feedbackErrorType,
                           @Param("feedbackComment") String feedbackComment,
                           @Param("feedbackAt") java.util.Date feedbackAt);

    TextToSqlRunEntity selectByConversationId(@Param("conversationId") String conversationId);

    TextToSqlRunEntity selectById(@Param("id") Long id);

    List<TextToSqlRunEntity> selectPage(TextToSqlRunQueryBo query);
}

