package cn.yanque.models.ai.texttosql.biz;

import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;

public interface TextToSqlBiz {

    TextToSqlRouteDto.RouteRes route(TextToSqlRouteDto.RouteReq req, Long userId);

    TextToSqlRouteDto.RouteRes route(TextToSqlRouteDto.RouteReq req, Long userId, String sourceType);

    TextToSqlRouteDto.RouteRes continueQuestion(TextToSqlRouteDto.ContinueReq req);
}
