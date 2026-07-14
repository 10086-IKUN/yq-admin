package cn.yanque.models.ai.texttosql.biz.impl;

import cn.yanque.models.ai.texttosql.biz.TextToSqlBiz;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlContinueReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRouteReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRes;
import cn.yanque.models.ai.texttosql.service.PythonTextToSqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextToSqlBizImpl implements TextToSqlBiz {

    @Autowired
    private PythonTextToSqlClient pythonTextToSqlClient;

    @Override
    public TextToSqlRes route(TextToSqlRouteReq req) {
        req.setUserQuestion(req.getUserQuestion().trim());
        return pythonTextToSqlClient.route(req);
    }

    @Override
    public TextToSqlRes continueQuestion(TextToSqlContinueReq req) {
        req.setConversationId(req.getConversationId().trim());
        req.setUserAnswer(req.getUserAnswer().trim());
        return pythonTextToSqlClient.continueQuestion(req);
    }
}
