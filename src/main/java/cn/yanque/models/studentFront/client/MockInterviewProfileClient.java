package cn.yanque.models.studentFront.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.interview.config.InterviewAiProperties;
import cn.yanque.models.studentFront.pojo.dto.MockInterviewProfileGenerateReq;
import cn.yanque.models.studentFront.pojo.dto.MockInterviewProfileGenerateRes;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockInterviewProfileClient {

    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    @Autowired
    private InterviewAiProperties aiChatProperties;

    public MockInterviewProfileGenerateRes generate(MockInterviewProfileGenerateReq req) {
        try (HttpResponse response = HttpRequest.post(buildUrl())
                .contentType(CONTENT_TYPE_JSON)
                .setConnectionTimeout(toMillis(aiChatProperties.getConnectTimeoutSeconds(), 3))
                .setReadTimeout(toMillis(aiChatProperties.getResponseTimeoutSeconds(), 120))
                .body(JSON.toJSONString(req))
                .execute()) {
            if (response == null || !response.isOk()) {
                throw BusinessException.RemoteError.newInstance("模拟面试画像生成失败");
            }
            return JSON.parseObject(response.body(), MockInterviewProfileGenerateRes.class);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw BusinessException.RemoteError.newInstance("模拟面试画像服务调用异常");
        }
    }

    private String buildUrl() {
        String baseUrl = aiChatProperties.getBaseUrl();
        String path = aiChatProperties.getMockInterviewProfileGeneratePath();
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private int toMillis(Integer seconds, int defaultSeconds) {
        int safeSeconds = seconds == null || seconds <= 0 ? defaultSeconds : seconds;
        return safeSeconds * 1000;
    }
}
