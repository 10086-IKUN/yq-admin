package cn.yanque.models.studentFront.ai.biz.impl;

import cn.yanque.models.studentFront.ai.biz.StudentAiChatBiz;
import cn.yanque.models.studentFront.ai.config.AiChatProperties;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatSessionEntity;
import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatCreateSessionReq;
import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatSendReq;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatMessageRes;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatSessionRes;
import cn.yanque.models.studentFront.ai.service.AiChatPythonClient;
import cn.yanque.models.studentFront.ai.service.StudentAiChatService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component

/**
 * 学生端 AI 问答业务编排。
 *
 * <p>这一层负责把 HTTP SSE、会话校验、消息落库和 Python 流式响应串起来；
 * 真正的数据读写交给 {@link StudentAiChatService}，模型调用交给 {@link AiChatPythonClient}。</p>
 */
public class StudentAiChatBizImpl implements StudentAiChatBiz {

    private static final String DEFAULT_ERROR_MESSAGE = "AI 服务暂时不可用，请稍后重试";

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private StudentAiChatService studentAiChatService;

    @Autowired
    private AiChatPythonClient aiChatPythonClient;

    @Autowired
    private AiChatProperties properties;

    @Override
    public List<AiChatSessionRes> listSessions(Long studentId) {
        return studentAiChatService.listSessions(studentId)
                .stream()
                .map(this::toSessionRes)
                .toList();
    }

    @Override
    public AiChatSessionRes createSession(Long studentId, String studentCode, AiChatCreateSessionReq req) {
        String title = req == null ? null : req.getTitle();
        return toSessionRes(studentAiChatService.createSession(studentId, studentCode, title));
    }

    @Override
    public List<AiChatMessageRes> listMessages(Long sessionId, Long studentId) {
        return studentAiChatService.listMessages(sessionId, studentId)
                .stream()
                .map(this::toMessageRes)
                .toList();
    }

    @Override
    public void deleteSession(Long sessionId, Long studentId) {
        studentAiChatService.deleteSession(sessionId, studentId);
    }

    @Override
    public SseEmitter stream(Long studentId, String studentCode, AiChatSendReq req) {
        long timeout = TimeUnit.SECONDS.toMillis(properties.getResponseTimeoutSeconds() + 30L);
        SseEmitter emitter = new SseEmitter(timeout);
        executorService.execute(() -> doStream(studentId, studentCode, req, emitter));
        return emitter;
    }

    /**
     * 执行一次学生端 AI 问答流式调用。
     *
     * <p>流程固定为：确认会话 -> 保存用户消息 -> 调用 Python 流式接口 -> 转发 chunk -> 保存助手完整回复。</p>
     */
    private void doStream(Long studentId, String studentCode, AiChatSendReq req, SseEmitter emitter) {
        String question = req.getMessage().trim();
        AiChatSessionEntity session = null;
        try {
            session = prepareSession(studentId, studentCode, req, question);
            List<AiChatMessageEntity> history = studentAiChatService.listRecentHistory(
                    session.getId(),
                    studentId,
                    properties.getHistoryLimit());

            AiChatMessageEntity userMessage = buildMessage(session.getId(), studentId, "user", question, null, null, null);
            studentAiChatService.addMessage(userMessage);
            studentAiChatService.refreshSessionStats(session.getId());
            sendEvent(emitter, "session", buildSessionPayload(session.getId(), studentId, userMessage.getId()));

            StringBuilder answer = new StringBuilder();
            AtomicReference<String> model = new AtomicReference<>();
            AtomicReference<Integer> tokens = new AtomicReference<>(0);
            AtomicBoolean failed = new AtomicBoolean(false);

            aiChatPythonClient.stream(studentId, session.getId(), question, history, (event, data) -> {
                if ("message_start".equals(event)) {
                    model.set(data.getString("model"));
                    sendEvent(emitter, "message_start", data);
                    return;
                }
                if ("chunk".equals(event)) {
                    String content = data.getString("content");
                    if (content != null && !content.isEmpty()) {
                        answer.append(content);
                        sendEvent(emitter, "chunk", Map.of("content", content));
                    }
                    return;
                }
                if ("done".equals(event)) {
                    // Python 的 done 只表示模型输出结束；Java 在这里统一完成 assistant 消息落库和前端收尾事件。
                    model.set(data.getString("model"));
                    Integer totalTokens = data.getInteger("tokens");
                    if (totalTokens != null) {
                        tokens.set(totalTokens);
                    }
                    return;
                }
                if ("error".equals(event)) {
                    failed.set(true);
                    String message = data.getString("message");
                    sendEvent(emitter, "error", Map.of("message", normalizeErrorMessage(message)));
                }
            });

            if (!failed.get()) {
                AiChatMessageEntity assistantMessage = buildMessage(
                        session.getId(),
                        studentId,
                        "assistant",
                        answer.toString(),
                        model.get(),
                        tokens.get(),
                        "stop");
                studentAiChatService.addMessage(assistantMessage);
                studentAiChatService.refreshSessionStats(session.getId());
                sendEvent(emitter, "done", buildDonePayload(session.getId(), studentId, assistantMessage));
            }
            emitter.complete();
        } catch (Exception ex) {
            Long sessionId = session == null ? req.getSessionId() : session.getId();
            log.error("AI chat stream failed, studentId={}, sessionId={}", studentId, sessionId, ex);
            sendErrorSilently(emitter, DEFAULT_ERROR_MESSAGE);
            emitter.complete();
        }
    }

    private AiChatSessionEntity prepareSession(Long studentId, String studentCode, AiChatSendReq req, String question) {
        if (req.getSessionId() != null) {
            return studentAiChatService.getSession(req.getSessionId(), studentId);
        }
        return studentAiChatService.createSession(studentId, studentCode, buildTitle(question));
    }

    /**
     * 构造待入库的聊天消息。
     *
     * <p>contentType 固定为 markdown，方便前端统一用 Markdown 渲染 AI 回复。</p>
     */
    private AiChatMessageEntity buildMessage(Long sessionId,
                                             Long studentId,
                                             String role,
                                             String content,
                                             String model,
                                             Integer tokens,
                                             String finishReason) {
        AiChatMessageEntity entity = new AiChatMessageEntity();
        entity.setSessionId(sessionId);
        entity.setStudentId(studentId);
        entity.setRole(role);
        entity.setContent(content);
        entity.setContentType("markdown");
        entity.setTokenCount(tokens == null ? 0 : tokens);
        entity.setModelName(model);
        entity.setFinishReason(finishReason);
        entity.setCompressed(false);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    private String buildTitle(String question) {
        String value = question.replaceAll("\\s+", " ").trim();
        if (value.isEmpty()) {
            return "新对话";
        }
        return value.length() > 40 ? value.substring(0, 40) : value;
    }

    private Map<String, Object> buildSessionPayload(Long sessionId, Long studentId, Long userMessageId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("session", toSessionRes(studentAiChatService.getSession(sessionId, studentId)));
        payload.put("userMessageId", userMessageId);
        return payload;
    }

    /**
     * 构造发送给前端的 done 事件。
     *
     * <p>前端收到该事件后会结束 loading 状态，并把本次助手消息补入本地消息列表。</p>
     */
    private Map<String, Object> buildDonePayload(Long sessionId, Long studentId, AiChatMessageEntity assistantMessage) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("session", toSessionRes(studentAiChatService.getSession(sessionId, studentId)));
        payload.put("messageId", assistantMessage.getId());
        payload.put("model", assistantMessage.getModelName());
        payload.put("tokens", assistantMessage.getTokenCount());
        return payload;
    }

    private AiChatSessionRes toSessionRes(AiChatSessionEntity entity) {
        AiChatSessionRes res = new AiChatSessionRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private AiChatMessageRes toMessageRes(AiChatMessageEntity entity) {
        AiChatMessageRes res = new AiChatMessageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private void sendEvent(SseEmitter emitter, String event, Object data) throws IOException {
        emitter.send(SseEmitter.event().name(event).data(data));
    }

    private void sendErrorSilently(SseEmitter emitter, String message) {
        try {
            sendEvent(emitter, "error", Map.of("message", message));
        } catch (IOException ignored) {
            // Client disconnected.
        }
    }

    private String normalizeErrorMessage(String message) {
        return message == null || message.isBlank() ? DEFAULT_ERROR_MESSAGE : message;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }
}
