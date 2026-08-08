package cn.yanque.models.ai.texttosql.service;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.util.RequestGuidPropagation;
import cn.yanque.models.ai.texttosql.config.TextToSqlProperties;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class PythonTextToSqlClient {

    @Autowired
    private TextToSqlProperties properties;

    public TextToSqlRouteDto.RouteRes route(TextToSqlRouteDto.RouteReq req) {
        return post(properties.getRoutePath(), req);
    }

    public TextToSqlRouteDto.RouteRes continueQuestion(TextToSqlRouteDto.ContinueReq req) {
        return post(properties.getContinuePath(), req);
    }

    private TextToSqlRouteDto.RouteRes post(String path, Object body) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    // Uvicorn does not support the clear-text HTTP/2 upgrade attempted by Java HttpClient.
                    // Force HTTP/1.1 so POST bodies are preserved instead of being retried as empty requests.
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(safeSeconds(properties.getConnectTimeoutSeconds(), 5)))
                    .build();
            HttpRequest request = RequestGuidPropagation.apply(HttpRequest.newBuilder(buildUri(path)))
                    .timeout(Duration.ofSeconds(safeSeconds(properties.getResponseTimeoutSeconds(), 180)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(body), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(502, "Text-to-SQL 服务返回 HTTP " + response.statusCode() + ": " + response.body());
            }
            return JSON.parseObject(response.body(), TextToSqlRouteDto.RouteRes.class);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            String detail = e.getMessage();
            if (detail == null || detail.isBlank()) {
                detail = e.getClass().getSimpleName();
            }
            throw new BusinessException(
                    502,
                    "无法连接 Text-to-SQL 服务 " + buildUri(path) + "：" + detail + "，请确认 Python 服务已启动"
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(502, "Text-to-SQL 服务调用被中断");
        }
    }

    private URI buildUri(String path) {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return URI.create(baseUrl.substring(0, baseUrl.length() - 1) + path);
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return URI.create(baseUrl + "/" + path);
        }
        return URI.create(baseUrl + path);
    }

    private long safeSeconds(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }
}
