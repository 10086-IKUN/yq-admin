package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalRunResultQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunResultEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlEvalRunResultMapper {

    void insert(TextToSqlEvalRunResultEntity entity);

    int updateById(TextToSqlEvalRunResultEntity entity);

    int markClarificationRunningById(@Param("id") Long id,
                                     @Param("failureType") String failureType,
                                     @Param("failureReason") String failureReason);

    int markClarificationRunErrorById(@Param("id") Long id,
                                      @Param("failureType") String failureType,
                                      @Param("failureReason") String failureReason,
                                      @Param("executionError") String executionError,
                                      @Param("durationMs") Long durationMs);

    TextToSqlEvalRunResultEntity selectById(@Param("id") Long id);

    List<TextToSqlEvalRunResultEntity> selectPage(TextToSqlEvalRunResultQueryBo query);

    List<TextToSqlEvalRunResultEntity> selectByEvalRunId(@Param("evalRunId") Long evalRunId);
}

