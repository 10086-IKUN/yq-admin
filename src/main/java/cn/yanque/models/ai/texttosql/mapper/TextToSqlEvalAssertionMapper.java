package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalAssertionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlEvalAssertionMapper {

    void insertBatch(@Param("list") List<TextToSqlEvalAssertionEntity> list);

    void deleteByEvalQuestionId(@Param("evalQuestionId") Long evalQuestionId);

    List<TextToSqlEvalAssertionEntity> selectByEvalQuestionId(@Param("evalQuestionId") Long evalQuestionId);

    List<TextToSqlEvalAssertionEntity> selectByEvalQuestionIds(@Param("ids") List<Long> ids);
}

