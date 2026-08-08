package cn.yanque.models.ai.texttosql.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalAssertionEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalQuestionEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionSaveReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionUpdateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalMetadataRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalQuestionRes;

import java.util.List;

public interface TextToSqlEvalQuestionService {

    List<TextToSqlEvalQuestionEntity> selectActiveEntitiesByIds(List<Long> ids);

    List<TextToSqlEvalAssertionEntity> selectAssertionEntitiesByQuestionId(Long evalQuestionId);

    TextToSqlEvalMetadataRes metadata();

    PageResult<TextToSqlEvalQuestionRes> page(TextToSqlEvalQuestionPageReq req);

    TextToSqlEvalQuestionRes detail(Long id);

    IdRes create(TextToSqlEvalQuestionSaveReq req, Long createdBy);

    IdRes createFromRun(Long runId, Long createdBy);

    void update(Long id, TextToSqlEvalQuestionUpdateReq req);
}

