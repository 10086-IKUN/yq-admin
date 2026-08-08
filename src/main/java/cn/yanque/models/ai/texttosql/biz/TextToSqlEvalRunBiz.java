package cn.yanque.models.ai.texttosql.biz;

import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunClarifyReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunCreateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunResultRes;

public interface TextToSqlEvalRunBiz {

    IdRes createAndRun(TextToSqlEvalRunCreateReq req, Long createdBy);

    TextToSqlEvalRunResultRes clarifyResult(Long resultId, TextToSqlEvalRunClarifyReq req);
}

