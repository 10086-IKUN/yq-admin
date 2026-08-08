package cn.yanque.models.ai.knowledge.service;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.util.RequestGuidPropagation;
import cn.yanque.models.ai.knowledge.config.AiKnowledgeProperties;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeChunksReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeChunksRes;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeIndexReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeIndexRes;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchRes;
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
public class PythonKnowledgeClient {

    @Autowired
    private AiKnowledgeProperties properties;

    public PythonKnowledgeIndexRes indexDocument(PythonKnowledgeIndexReq req) {
        return post(properties.getIndexPath(), req, PythonKnowledgeIndexRes.class);
    }

    public PythonKnowledgeSearchRes search(PythonKnowledgeSearchReq req) {
        return post(properties.getSearchPath(), req, PythonKnowledgeSearchRes.class);
    }

    public PythonKnowledgeChunksRes listDocumentChunks(PythonKnowledgeChunksReq req) {
        return post(properties.getChunksPath(), req, PythonKnowledgeChunksRes.class);
    }

    private <T> T post(String path, Object req, Class<T> responseType) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    // Uvicorn does not support the clear-text HTTP/2 upgrade attempted by Java HttpClient.
                    // Force HTTP/1.1 so POST bodies are not retried as empty requests.
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(safeSeconds(properties.getConnectTimeoutSeconds(), 5)))
                    .build();
            HttpRequest request = RequestGuidPropagation.apply(HttpRequest.newBuilder(buildUri(path)))
                    .timeout(Duration.ofSeconds(safeSeconds(properties.getResponseTimeoutSeconds(), 180)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(req), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(502, "knowledge service returned HTTP " + response.statusCode() + ": " + response.body());
            }
            return JSON.parseObject(response.body(), responseType);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException(502, "failed to call knowledge service: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(502, "knowledge service call interrupted");
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
