package cn.yanque.models.ai.texttosql.biz;

import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlContinueReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRouteReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRes;

public interface TextToSqlBiz {

    TextToSqlRes route(TextToSqlRouteReq req);

    TextToSqlRes continueQuestion(TextToSqlContinueReq req);
}
