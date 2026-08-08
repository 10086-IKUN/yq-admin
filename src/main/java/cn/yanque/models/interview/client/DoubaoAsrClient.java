package cn.yanque.models.interview.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.interview.config.DoubaoAsrProperties;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DoubaoAsrClient {
    private static final String OK = "20000000";
    private static final String QUEUING = "20000001";
    private static final String PROCESSING = "20000002";

    @Autowired
    private DoubaoAsrProperties properties;

    public String submit(Long studentId, String audioUrl, String fileName) {
        validateConfig();
        String taskId = UUID.randomUUID().toString();
        JSONObject body = new JSONObject();
        body.put("user", JSONObject.of("uid", "student_" + studentId));
        body.put("audio", JSONObject.of("url", audioUrl, "format", extension(fileName)));
        JSONObject options = new JSONObject();
        options.put("model_name", "bigmodel");
        options.put("enable_punc", true);
        options.put("enable_itn", true);
        options.put("enable_ddc", true);
        options.put("show_utterances", true);
        options.put("enable_speaker_info", true);
        body.put("request", options);
        try (HttpResponse response = request(properties.getSubmitUrl(), taskId)
                .body(body.toJSONString()).execute()) {
            if (!OK.equals(response.header("X-Api-Status-Code"))) {
                throw new BusinessException(502, "提交豆包语音转写失败：" + responseMessage(response));
            }
        }
        return taskId;
    }

    public QueryResult query(String taskId) {
        validateConfig();
        try (HttpResponse response = request(properties.getQueryUrl(), taskId).body("{}").execute()) {
            String code = response.header("X-Api-Status-Code");
            if (QUEUING.equals(code) || PROCESSING.equals(code)) {
                return QueryResult.processing();
            }
            if (!OK.equals(code)) {
                return QueryResult.failed(responseMessage(response));
            }
            JSONObject result = JSON.parseObject(response.body()).getJSONObject("result");
            if (result == null) {
                return QueryResult.processing();
            }
            JSONArray dialogue = new JSONArray();
            JSONArray utterances = result.getJSONArray("utterances");
            if (utterances != null) {
                for (int i = 0; i < utterances.size(); i++) {
                    JSONObject utterance = utterances.getJSONObject(i);
                    String text = utterance.getString("text");
                    if (text == null || text.isBlank()) {
                        continue;
                    }
                    String speaker = firstNotBlank(utterance.getString("speaker"),
                            utterance.getString("speaker_id"), utterance.getString("speakerId"));
                    JSONObject additions = utterance.getJSONObject("additions");
                    if (speaker == null && additions != null) {
                        speaker = firstNotBlank(additions.getString("speaker"), additions.getString("speaker_id"));
                    }
                    dialogue.add(JSONObject.of("speaker", speaker == null ? "UNKNOWN" : speaker, "text", text.trim()));
                }
            }
            return dialogue.isEmpty() ? QueryResult.failed("语音识别结果为空") : QueryResult.success(dialogue.toJSONString());
        }
    }

    public int batchSize() {
        return properties.getQueryBatchSize() == null || properties.getQueryBatchSize() <= 0
                ? 10 : properties.getQueryBatchSize();
    }

    private HttpRequest request(String url, String taskId) {
        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("X-Api-Resource-Id", properties.getResourceId())
                .header("X-Api-Request-Id", taskId)
                .header("X-Api-Sequence", "-1")
                .timeout((properties.getRequestTimeoutSeconds() == null ? 60 : properties.getRequestTimeoutSeconds()) * 1000);
        if (notBlank(properties.getApiKey())) {
            request.header("X-Api-Key", properties.getApiKey());
        } else {
            request.header("X-Api-App-Key", properties.getAppKey());
            request.header("X-Api-Access-Key", properties.getAccessKey());
        }
        return request;
    }

    private void validateConfig() {
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            throw new BusinessException(400, "豆包语音转写未启用");
        }
        if (!notBlank(properties.getApiKey()) && (!notBlank(properties.getAppKey()) || !notBlank(properties.getAccessKey()))) {
            throw new BusinessException(500, "豆包语音转写密钥未配置");
        }
    }

    private String responseMessage(HttpResponse response) {
        String message = response.header("X-Api-Message");
        if (notBlank(message)) return message;
        String body = response.body();
        return body == null || body.isBlank() ? "未知错误" : body.substring(0, Math.min(body.length(), 300));
    }

    private String extension(String name) {
        if (name == null || !name.contains(".")) return "mp3";
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    private String firstNotBlank(String... values) {
        for (String value : values) if (notBlank(value)) return value;
        return null;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    public record QueryResult(Status status, String dialogueJson, String errorMessage) {
        public static QueryResult processing() { return new QueryResult(Status.PROCESSING, null, null); }
        public static QueryResult success(String dialogue) { return new QueryResult(Status.SUCCESS, dialogue, null); }
        public static QueryResult failed(String message) { return new QueryResult(Status.FAILED, null, message); }
    }

    public enum Status { PROCESSING, SUCCESS, FAILED }
}
