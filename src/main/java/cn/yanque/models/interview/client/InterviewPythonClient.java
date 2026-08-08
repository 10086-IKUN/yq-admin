package cn.yanque.models.interview.client;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.util.RequestGuidPropagation;
import cn.yanque.models.interview.config.InterviewAiProperties;
import cn.yanque.models.interview.pojo.InterviewReviewRecordEntity;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;

@Component
public class InterviewPythonClient {
    @Autowired
    private InterviewAiProperties properties;

    public String polish(String dialogueJson) {
        JSONObject request = JSONObject.of("dialogue", JSON.parseArray(dialogueJson));
        JSONObject response = post(properties.getPolishPath(), request);
        JSONArray dialogue = response.getJSONArray("dialogue");
        if (dialogue == null || dialogue.isEmpty()) {
            throw new BusinessException(502, "AI 对话清洗结果为空");
        }
        return dialogue.toJSONString();
    }

    public String generateReport(InterviewReviewRecordEntity record, String dialogueJson) {
        JSONObject request = new JSONObject();
        String resume = record.getResumeText();
        request.put("resume_text", resume == null || resume.isBlank()
                ? "学生暂未提供简历，请仅依据面试对话进行复盘。" : resume.trim());
        request.put("dialogue", JSON.parseArray(dialogueJson));
        request.put("review_remark", record.getReviewRemark());
        request.put("interview_time", record.getInterviewTime() == null ? null
                : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getInterviewTime()));
        JSONObject report = post(properties.getReportPath(), request).getJSONObject("report");
        if (report == null || report.isEmpty()) {
            throw new BusinessException(502, "AI 面试复盘报告为空");
        }
        return report.toJSONString();
    }

    public JSONArray extractQuestions(InterviewReviewRecordEntity record) {
        JSONObject request = new JSONObject();
        request.put("dialogue", JSON.parseArray(record.getTranscriptDialogueJson()));
        request.put("report", record.getReportJson() == null ? new JSONObject() : JSON.parseObject(record.getReportJson()));
        request.put("interviewRole", record.getInterviewRole());
        request.put("companyName", record.getCompanyName());
        JSONArray questions = post(properties.getQuestionExtractPath(), request).getJSONArray("questions");
        return questions == null ? new JSONArray() : questions;
    }

    public JSONObject findSimilarQuestion(String question) {
        JSONArray hits = searchSimilarQuestions(question, 3);
        return hits == null || hits.isEmpty() ? null : hits.getJSONObject(0);
    }

    public JSONArray searchSimilarQuestions(String question, int limit) {
        JSONObject request = JSONObject.of("question", question, "limit", Math.max(1, Math.min(limit, 50)));
        JSONArray hits = post(properties.getQuestionVectorSearchPath(), request).getJSONArray("hits");
        return hits == null ? new JSONArray() : hits;
    }

    public void upsertQuestionVector(Long questionId, String question, String category, JSONArray tags) {
        JSONObject request = new JSONObject();
        request.put("questionId", questionId);
        request.put("question", question);
        request.put("category", category);
        request.put("tags", tags == null ? new JSONArray() : tags);
        post(properties.getQuestionVectorUpsertPath(), request);
    }

    private JSONObject post(String path, JSONObject body) {
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(safe(properties.getConnectTimeoutSeconds(), 5))).build();
            HttpRequest request = RequestGuidPropagation.apply(HttpRequest.newBuilder(uri(path)))
                    .timeout(Duration.ofSeconds(safe(properties.getResponseTimeoutSeconds(), 180)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString(), StandardCharsets.UTF_8)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(502, "Python 面试服务返回 HTTP " + response.statusCode() + "：" + truncate(response.body()));
            }
            return JSON.parseObject(response.body());
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException(502, "无法连接 Python 面试服务：" + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(502, "Python 面试服务调用被中断");
        }
    }

    private URI uri(String path) {
        String base = properties.getBaseUrl();
        if (base.endsWith("/") && path.startsWith("/")) return URI.create(base.substring(0, base.length() - 1) + path);
        if (!base.endsWith("/") && !path.startsWith("/")) return URI.create(base + "/" + path);
        return URI.create(base + path);
    }

    private int safe(Integer value, int fallback) { return value == null || value <= 0 ? fallback : value; }
    private String truncate(String value) { return value == null ? "" : value.substring(0, Math.min(value.length(), 300)); }
}
