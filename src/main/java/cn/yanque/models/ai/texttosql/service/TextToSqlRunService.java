package cn.yanque.models.ai.texttosql.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRunRes;

public interface TextToSqlRunService {

    void createRunning(String conversationId, String originalQuestion, Long createdBy);

    void createRunning(String conversationId, String originalQuestion, Long createdBy, String sourceType);

    void markRunning(String conversationId);

    void saveResultAsync(String conversationId, TextToSqlRouteDto.RouteRes res);

    void saveFailure(String conversationId, String errorMessage);

    PageResult<TextToSqlRunRes> page(TextToSqlRunPageReq req);

    TextToSqlRunRes detail(Long id);
}

