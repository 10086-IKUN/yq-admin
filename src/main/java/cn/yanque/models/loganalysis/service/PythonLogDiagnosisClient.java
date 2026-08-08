package cn.yanque.models.loganalysis.service;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.util.RequestGuidPropagation;
import cn.yanque.models.loganalysis.config.LogDiagnosisProperties;
import cn.yanque.models.loganalysis.pojo.vo.req.LogDiagnosisReq;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PythonLogDiagnosisClient {

    private final LogDiagnosisProperties properties;

    public JSONObject context(LogDiagnosisReq request) {
        return post(properties.getContextPath(), request);
    }

    public JSONObject analyze(LogDiagnosisReq request) {
        return post(properties.getAnalyzePath(), request);
    }

    private JSONObject post(String path, LogDiagnosisReq body) {
        URI uri = buildUri(path);
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(safeSeconds(properties.getConnectTimeoutSeconds(), 5)))
                    .build();
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(safeSeconds(properties.getResponseTimeoutSeconds(), 180)))
                    .header("Content-Type", "application/json");
            HttpRequest request = RequestGuidPropagation.apply(builder)
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(body), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(
                        502,
                        "Python 日志诊断服务返回 HTTP " + response.statusCode() + ": " + errorDetail(response.body())
                );
            }
            return JSON.parseObject(response.body());
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new BusinessException(502, "无法连接 Python 日志诊断服务 " + uri + ": " + safeMessage(exception));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException(502, "Python 日志诊断服务调用被中断");
        } catch (RuntimeException exception) {
            throw new BusinessException(502, "Python 日志诊断服务响应格式不正确: " + safeMessage(exception));
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

    private long safeSeconds(Integer value, int fallback) {
        return value == null || value <= 0 ? fallback : value;
    }

    private String errorDetail(String body) {
        try {
            String detail = JSON.parseObject(body).getString("detail");
            return detail == null || detail.isBlank() ? truncate(body) : truncate(detail);
        } catch (Exception ignored) {
            return truncate(body);
        }
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 500 ? value : value.substring(0, 500) + "...";
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }
}
