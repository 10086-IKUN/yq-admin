package cn.yanque.models.ai.texttosql.service;

import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlFeedbackReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlFeedbackRes;

public interface TextToSqlFeedbackService {

    TextToSqlFeedbackRes create(TextToSqlFeedbackReq req, Long createdBy);
}

