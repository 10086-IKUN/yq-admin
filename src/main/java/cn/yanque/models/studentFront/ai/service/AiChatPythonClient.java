package cn.yanque.models.studentFront.ai.service;

import cn.yanque.common.util.RequestGuidPropagation;
import cn.yanque.models.studentFront.ai.config.AiChatProperties;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component

/**
 * Python AI 问答服务客户端。
 *
 * <p>负责把 Java 侧的学生、会话、问题和历史消息组装成请求体，调用 Python 服务的 SSE 接口，
 * 再把流式事件解析成统一的回调交给业务层处理。</p>
 */
public class AiChatPythonClient {

    @Autowired
    private AiChatProperties properties;

    public void stream(Long studentId,
                       Long sessionId,
                       String message,
                       String summary,
                       List<AiChatMessageEntity> history,
                       AiChatStreamListener listener) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = RequestGuidPropagation.apply(HttpRequest.newBuilder(buildUri(properties.getStreamPath())))
                .timeout(Duration.ofSeconds(properties.getResponseTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        buildRequestBody(studentId, sessionId, message, summary, history),
                        StandardCharsets.UTF_8))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("AI service returned HTTP " + response.statusCode());
        }
        parseSse(response.body(), listener);
    }

    public String summarize(Long studentId,
                            Long sessionId,
                            String oldSummary,
                            List<AiChatMessageEntity> messages) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = RequestGuidPropagation.apply(HttpRequest.newBuilder(buildUri(properties.getSummarizePath())))
                .timeout(Duration.ofSeconds(properties.getResponseTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        buildSummarizeRequestBody(studentId, sessionId, oldSummary, messages),
                        StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("AI summarize service returned HTTP " + response.statusCode());
        }
        JSONObject payload = JSON.parseObject(response.body());
        String summary = payload.getString("summary");
        if (summary == null || summary.isBlank()) {
            throw new IOException("AI summarize service returned empty summary");
        }
        return summary.trim();
    }

    /**
     * 拼接 Python AI 服务的流式接口地址。
     *
     * <p>baseUrl 和 streamPath 分开配置，方便本地、测试和线上环境只替换主机地址。</p>
     */
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

    private String buildRequestBody(Long studentId,
                                    Long sessionId,
                                    String message,
                                    String summary,
                                    List<AiChatMessageEntity> history) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("studentId", studentId);
        body.put("sessionId", sessionId);
        body.put("message", message);
        body.put("summary", summary);
        body.put("history", buildHistory(history));
        return JSON.toJSONString(body);
    }

    private String buildSummarizeRequestBody(Long studentId,
                                             Long sessionId,
                                             String oldSummary,
                                             List<AiChatMessageEntity> messages) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("studentId", studentId);
        body.put("sessionId", sessionId);
        body.put("oldSummary", oldSummary);
        body.put("messages", buildHistory(messages));
        return JSON.toJSONString(body);
    }

    /**
     * 构造传给模型的上下文历史。
     *
     * <p>这里只传 user/assistant 消息，系统提示词由 Python 服务统一补齐，避免 Java 和 Python 两端提示词漂移。</p>
     */
    private List<Map<String, String>> buildHistory(List<AiChatMessageEntity> history) {
        List<Map<String, String>> result = new ArrayList<>();
        for (AiChatMessageEntity item : history) {
            if (!"user".equals(item.getRole()) && !"assistant".equals(item.getRole())) {
                continue;
            }
            if (item.getContent() == null || item.getContent().isBlank()) {
                continue;
            }
            Map<String, String> message = new LinkedHashMap<>();
            message.put("role", item.getRole());
            message.put("content", item.getContent());
            result.add(message);
        }
        return result;
    }

    /**
     * 解析 Python 返回的 SSE 文本流。
     *
     * <p>每个空行表示一个 SSE 事件结束，解析后交给业务层决定如何转发和落库。</p>
     */
    private void parseSse(InputStream inputStream, AiChatStreamListener listener) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String event = "message";
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    dispatch(event, data, listener);
                    event = "message";
                    data.setLength(0);
                    continue;
                }
                if (line.startsWith("event:")) {
                    event = line.substring("event:".length()).trim();
                    continue;
                }
                if (line.startsWith("data:")) {
                    if (data.length() > 0) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                }
            }
            dispatch(event, data, listener);
        }
    }

    /**
     * 将单个 SSE 事件反序列化为 JSON。
     *
     * <p>如果模型侧返回了非 JSON 内容，也保留 raw 字段，方便排查链路问题。</p>
     */
    private void dispatch(String event, StringBuilder data, AiChatStreamListener listener) throws IOException {
        if (data.length() == 0) {
            return;
        }
        JSONObject payload;
        try {
            payload = JSON.parseObject(data.toString());
        } catch (Exception ex) {
            payload = new JSONObject();
            payload.put("raw", data.toString());
        }
        listener.onEvent(event, payload);
    }

    public interface AiChatStreamListener {
        /**
         * 接收 Python 服务输出的单个 SSE 事件。
         */
        void onEvent(String event, JSONObject data) throws IOException;
    }
}
