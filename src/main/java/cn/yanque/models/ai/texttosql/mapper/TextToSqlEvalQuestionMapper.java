package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalQuestionQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalQuestionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlEvalQuestionMapper {

    void insert(TextToSqlEvalQuestionEntity entity);

    int updateById(TextToSqlEvalQuestionEntity entity);

    TextToSqlEvalQuestionEntity selectById(@Param("id") Long id);

    TextToSqlEvalQuestionEntity selectBySourceRunId(@Param("sourceRunId") Long sourceRunId);

    List<TextToSqlEvalQuestionEntity> selectPage(TextToSqlEvalQuestionQueryBo query);

    List<TextToSqlEvalQuestionEntity> selectActiveByIds(@Param("ids") List<Long> ids);
}

