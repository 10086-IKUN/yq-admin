package cn.yanque.models.studentFront.websocket;

import cn.yanque.models.mockinterview.mapper.MockInterviewSessionMapper;
import cn.yanque.models.studentFront.client.DoubaoRealtimeVoiceClient;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class MockInterviewVoiceWebSocketHandler extends BinaryWebSocketHandler {

    /** 记录前端已经上传了多少段麦克风音频，主要用于页面状态展示和日志排查。 */
    private static final String ATTR_AUDIO_CHUNK_COUNT = "audioChunkCount";

    /** 记录前端已经上传了多少字节麦克风音频，主要用于页面状态展示和日志排查。 */
    private static final String ATTR_AUDIO_TOTAL_BYTES = "audioTotalBytes";

    /**
     * Spring/Tomcat 的 WebSocketSession 不支持多个线程同时 sendMessage。
     * 火山回调线程和前端音频处理线程可能同时给浏览器发消息，所以同一个 session 必须串行发送。
     */
    private static final String ATTR_SEND_LOCK = "sendLock";

    @Autowired
    private DoubaoRealtimeVoiceClient doubaoRealtimeVoiceClient;

    @Autowired
    private MockInterviewSessionMapper mockInterviewSessionMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 前端 WebSocket 建立后，初始化统计信息和发送锁。
        session.getAttributes().put(ATTR_AUDIO_CHUNK_COUNT, new AtomicInteger());
        session.getAttributes().put(ATTR_AUDIO_TOTAL_BYTES, new AtomicLong());
        session.getAttributes().put(ATTR_SEND_LOCK, new Object());

        // voiceSessionId 对应 Java 后端到火山 Realtime 的那条连接。
        // 这里注册监听器，把火山返回的文本和音频继续转发给当前浏览器 WebSocket。
        String voiceSessionId = String.valueOf(session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_VOICE_SESSION_ID));
        doubaoRealtimeVoiceClient.registerListener(voiceSessionId, (id, eventText) -> forwardDoubaoEvent(session, eventText));
        sendJson(session, "VOICE_WS_READY", JSONObject.of(
                "sessionId", session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_SESSION_ID),
                "voiceSessionId", voiceSessionId
        ));
        if (!doubaoRealtimeVoiceClient.sayHello(voiceSessionId)) {
            log.warn("mock interview voice hello was not sent, voiceSessionId={}", voiceSessionId);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // 浏览器发来的二进制消息就是麦克风 PCM 音频帧。
        // 这里不做语音识别，只统计后直接转发给火山 Realtime。
        AtomicInteger chunkCount = (AtomicInteger) session.getAttributes().get(ATTR_AUDIO_CHUNK_COUNT);
        AtomicLong totalBytes = (AtomicLong) session.getAttributes().get(ATTR_AUDIO_TOTAL_BYTES);
        int bytes = message.getPayloadLength();
        int chunks = chunkCount.incrementAndGet();
        long total = totalBytes.addAndGet(bytes);
        String voiceSessionId = String.valueOf(session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_VOICE_SESSION_ID));
        boolean forwarded = doubaoRealtimeVoiceClient.forwardAudio(voiceSessionId, readBytes(message.getPayload()));
        if (chunks == 1 || chunks % 10 == 0) {
            log.info("mock interview voice audio received, sessionId={}, voiceSessionId={}, chunks={}, totalBytes={}, forwarded={}",
                    session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_SESSION_ID),
                    voiceSessionId,
                    chunks,
                    total,
                    forwarded);
        }
        sendJson(session, "AUDIO_RECEIVED", JSONObject.of(
                "bytes", bytes,
                "chunks", chunks,
                "totalBytes", total,
                "forwarded", forwarded
        ));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JSONObject payload = parseJson(message.getPayload());
            if ("AUDIO_COMMIT".equals(payload.getString("type"))) {
                // 前端检测到用户停顿后发送 AUDIO_COMMIT，表示这一轮回答结束。
                // 后端把这个提交事件转给火山，让火山开始生成面试官回复。
                String voiceSessionId = String.valueOf(session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_VOICE_SESSION_ID));
                boolean committed = doubaoRealtimeVoiceClient.commitAudio(voiceSessionId);
                sendJson(session, "AUDIO_COMMITTED", JSONObject.of("committed", committed));
                return;
            }
            sendJson(session, "TEXT_RECEIVED", JSONObject.of("text", message.getPayload()));
        } catch (IOException e) {
            log.warn("mock interview voice text ack failed", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 浏览器离开页面或结束通话后，同步关闭火山 Realtime 连接，避免后端残留语音会话。
        String voiceSessionId = String.valueOf(session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_VOICE_SESSION_ID));
        doubaoRealtimeVoiceClient.unregisterListener(voiceSessionId);
        doubaoRealtimeVoiceClient.close(voiceSessionId);
        log.info("mock interview voice websocket closed, sessionId={}, voiceSessionId={}, status={}",
                session.getAttributes().get(MockInterviewVoiceHandshakeInterceptor.ATTR_SESSION_ID),
                voiceSessionId,
                status);
    }

    private void forwardDoubaoEvent(WebSocketSession session, String eventText) {
        if (!session.isOpen()) {
            return;
        }
        try {
            JSONObject event = parseEvent(eventText);
            String audio = firstString(event, "audio", "delta_audio", "audio_delta");
            if (audio != null) {
                // 火山客户端内部为了统一事件对象，会把 PCM 音频临时放成 base64 字段。
                // 发给浏览器时不要再塞进 JSON，直接转成 BinaryMessage，前端按 PCM 播放。
                sendBinary(session, Base64.getDecoder().decode(audio));
                event.remove("audio");
            }

            // 文本和事件元信息继续走 JSON，方便前端展示实时转写、回答文本和状态。
            JSONObject data = new JSONObject();
            data.put("event", event);
            data.put("eventCode", event.getInteger("event"));
            data.put("eventType", firstString(event, "eventType", "type", "event_type", "event"));
            data.put("text", extractEventText(event));
            data.put("errorCode", event.getInteger("errorCode"));
            boolean serverError = "SERVER_ERROR".equals(event.getString("type"));
            sendJson(session, serverError ? "VOICE_ERROR" : "DOUBAO_EVENT", data);
        } catch (Exception e) {
            log.warn("forward doubao event failed", e);
        }
    }

    private JSONObject parseEvent(String eventText) {
        try {
            return JSONObject.parseObject(eventText);
        } catch (Exception e) {
            JSONObject event = new JSONObject();
            event.put("raw", eventText);
            return event;
        }
    }

    private String firstString(JSONObject object, String... keys) {
        for (String key : keys) {
            String value = object.getString(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
            JSONObject response = object.getJSONObject("response");
            if (response != null) {
                value = response.getString(key);
                if (value != null && !value.trim().isEmpty()) {
                    return value;
                }
            }
            JSONObject item = object.getJSONObject("item");
            if (item != null) {
                value = item.getString(key);
                if (value != null && !value.trim().isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * ASRResponse(451) 的识别文本可能位于 json.results[0].text，
     * 不能只读取协议帧最外层，否则浏览器看不到用户说的话。
     */
    private String extractEventText(JSONObject event) {
        String direct = firstString(event, "text", "delta", "transcript", "content", "sentence", "utterance");
        if (direct != null) {
            return direct;
        }
        return extractNestedText(event.get("json"));
    }

    private String extractNestedText(Object value) {
        if (value instanceof JSONObject object) {
            String direct = firstString(object, "text", "delta", "transcript", "content", "sentence", "utterance");
            if (direct != null) {
                return direct;
            }
            for (String key : new String[]{"results", "result", "response", "item", "data"}) {
                String nested = extractNestedText(object.get(key));
                if (nested != null) {
                    return nested;
                }
            }
        } else if (value instanceof JSONArray array) {
            for (Object item : array) {
                String nested = extractNestedText(item);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private void sendJson(WebSocketSession session, String type, JSONObject data) throws IOException {
        if (!session.isOpen()) {
            return;
        }
        JSONObject payload = new JSONObject();
        payload.put("type", type);
        payload.put("data", data);
        // 所有发给同一个浏览器 WebSocket 的消息都要串行化，避免 TEXT_PARTIAL_WRITING。
        Object sendLock = session.getAttributes().computeIfAbsent(ATTR_SEND_LOCK, key -> new Object());
        synchronized (sendLock) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload.toJSONString()));
            }
        }
    }

    private void sendBinary(WebSocketSession session, byte[] data) throws IOException {
        if (!session.isOpen() || data == null || data.length == 0) {
            return;
        }
        // 音频二进制和 JSON 文本共用同一把锁，因为底层是同一个 WebSocket session。
        Object sendLock = session.getAttributes().computeIfAbsent(ATTR_SEND_LOCK, key -> new Object());
        synchronized (sendLock) {
            if (session.isOpen()) {
                session.sendMessage(new BinaryMessage(data));
            }
        }
    }

    private byte[] readBytes(ByteBuffer buffer) {
        ByteBuffer copy = buffer.asReadOnlyBuffer();
        byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return bytes;
    }

    private JSONObject parseJson(String text) {
        try {
            return JSONObject.parseObject(text);
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
