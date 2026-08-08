package cn.yanque.models.ai.texttosql.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunResultEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunResultPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunResultRes;

import java.util.List;

public interface TextToSqlEvalRunService {

    void insertRun(TextToSqlEvalRunEntity entity);

    void updateRunById(TextToSqlEvalRunEntity entity);

    void updateRunSummaryById(TextToSqlEvalRunEntity entity, boolean clearFinishedAt);

    TextToSqlEvalRunResultEntity selectResultEntityById(Long id);

    List<TextToSqlEvalRunResultEntity> selectResultEntitiesByEvalRunId(Long evalRunId);

    void insertResult(TextToSqlEvalRunResultEntity entity);

    void updateResultById(TextToSqlEvalRunResultEntity entity);

    void markResultClarificationRunning(Long id, String failureType, String failureReason);

    void markResultClarificationRunError(Long id, String failureType, String failureReason, String executionError, Long durationMs);

    PageResult<TextToSqlEvalRunRes> page(TextToSqlEvalRunPageReq req);

    TextToSqlEvalRunRes detail(Long id);

    PageResult<TextToSqlEvalRunResultRes> resultPage(Long evalRunId, TextToSqlEvalRunResultPageReq req);
}

