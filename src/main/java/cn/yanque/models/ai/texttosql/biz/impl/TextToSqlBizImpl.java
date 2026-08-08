package cn.yanque.models.ai.texttosql.biz.impl;

import cn.yanque.models.ai.texttosql.biz.TextToSqlBiz;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import cn.yanque.models.ai.texttosql.service.PythonTextToSqlClient;
import cn.yanque.models.ai.texttosql.service.TextToSqlRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TextToSqlBizImpl implements TextToSqlBiz {

    @Autowired
    private PythonTextToSqlClient pythonTextToSqlClient;

    @Autowired
    private TextToSqlRunService textToSqlRunService;

    @Override
    public TextToSqlRouteDto.RouteRes route(TextToSqlRouteDto.RouteReq req, Long userId) {
        return route(req, userId, "USER");
    }

    @Override
    public TextToSqlRouteDto.RouteRes route(TextToSqlRouteDto.RouteReq req, Long userId, String sourceType) {
        req.setUserQuestion(req.getUserQuestion().trim());
        if (req.getConversationId() == null || req.getConversationId().isBlank()) {
            req.setConversationId(UUID.randomUUID().toString());
        }
        textToSqlRunService.createRunning(req.getConversationId(), req.getUserQuestion(), userId, sourceType);
        try {
            TextToSqlRouteDto.RouteRes res = pythonTextToSqlClient.route(req);
            textToSqlRunService.saveResultAsync(req.getConversationId(), res);
            return res;
        } catch (RuntimeException ex) {
            textToSqlRunService.saveFailure(req.getConversationId(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public TextToSqlRouteDto.RouteRes continueQuestion(TextToSqlRouteDto.ContinueReq req) {
        req.setConversationId(req.getConversationId().trim());
        req.setUserAnswer(req.getUserAnswer().trim());
        textToSqlRunService.markRunning(req.getConversationId());
        try {
            TextToSqlRouteDto.RouteRes res = pythonTextToSqlClient.continueQuestion(req);
            textToSqlRunService.saveResultAsync(req.getConversationId(), res);
            return res;
        } catch (RuntimeException ex) {
            textToSqlRunService.saveFailure(req.getConversationId(), ex.getMessage());
            throw ex;
        }
    }
}
