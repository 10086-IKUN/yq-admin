package cn.yanque.models.studentFront.client;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.config.DoubaoRealtimeVoiceProperties;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewVoiceStartRes;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.net.http.WebSocketHandshakeException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DoubaoRealtimeVoiceClient {

    private static final String PROVIDER = "DOUBAO_REALTIME_VOICE";

    /*
     * 下面这些 event 编号来自火山 Realtime 协议。
     *
     * 这里要注意两件事：
     * 1. Java 和火山之间虽然也是 WebSocket，但 WebSocket 里传的不是普通 JSON 文本，
     *    而是火山定义的“二进制协议帧”。
     * 2. 不同 event 表示不同业务动作，例如开始连接、开始会话、上传音频、服务端返回音频等。
     */
    private static final int EVENT_START_CONNECTION = 1;
    private static final int EVENT_FINISH_CONNECTION = 2;
    private static final int EVENT_CONNECTION_STARTED = 50;
    private static final int EVENT_START_SESSION = 100;
    private static final int EVENT_FINISH_SESSION = 102;
    private static final int EVENT_SESSION_STARTED = 150;
    private static final int EVENT_TASK_REQUEST = 200;
    private static final int EVENT_SAY_HELLO = 300;
    private static final int EVENT_SERVER_FULL_RESPONSE = 350;
    private static final int EVENT_SERVER_AUDIO_ONLY_RESPONSE = 352;

    @Autowired
    private DoubaoRealtimeVoiceProperties properties;

    /*
     * voiceSessionId -> Java 到火山的 WebSocket 连接。
     *
     * 前端浏览器会先调用 /voice/start，后端在 start() 里生成 voiceSessionId，
     * 然后用这个 voiceSessionId 保存火山 WebSocket。
     *
     * 后面浏览器再连接 Java WebSocket 时，也会把同一个 voiceSessionId 带上来。
     * 这样 Java 就知道：当前浏览器发来的音频，应该转发到哪一条火山连接。
     */
    private final ConcurrentMap<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();

    /*
     * voiceSessionId -> 事件监听器。
     *
     * 火山返回 ASR 文本、模型回复、TTS 音频时，DoubaoRealtimeVoiceClient 自己不直接认识前端 session。
     * 所以 MockInterviewVoiceWebSocketHandler 会注册一个 listener。
     * 这里收到火山事件后，通过 listener 再把事件转回浏览器 WebSocket。
     */
    private final ConcurrentMap<String, DoubaoRealtimeVoiceEventListener> listenerMap = new ConcurrentHashMap<>();

    /*
     * 用 CompletableFuture 等待火山启动事件。
     *
     * 火山连接不是“WebSocket 握手成功”就能马上发音频，还需要：
     * 1. 发送 StartConnection
     * 2. 收到 EVENT_CONNECTION_STARTED，也就是 event=50
     * 3. 发送 StartSession
     * 4. 收到 EVENT_SESSION_STARTED，也就是 event=150
     *
     * 所以这里用两个 Future 把异步回调变成可等待的启动步骤。
     */
    private final ConcurrentMap<String, CompletableFuture<JSONObject>> connectionReadyMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CompletableFuture<JSONObject>> sessionReadyMap = new ConcurrentHashMap<>();

    public StudentMockInterviewVoiceStartRes start(Long interviewSessionId, String prompt) {
        // 这是我们自己系统里的语音会话 ID，用来把“浏览器 WebSocket”和“火山 WebSocket”对应起来。
        // 它不是火山返回的 ID，而是后端主动生成的 UUID。
        String voiceSessionId = UUID.randomUUID().toString();
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            // 本地未启用火山实时语音时，仍然返回一个 voiceSessionId，方便前端页面流程继续跑起来。
            return buildLocalReadyRes(voiceSessionId, interviewSessionId, prompt);
        }
        if (isBlank(properties.getWebsocketUrl()) || isBlank(properties.getAppId()) || isBlank(properties.getAccessToken())) {
            throw BusinessException.DateError.newInstance("火山实时语音配置不完整");
        }

        try {
            log.info("doubao realtime voice connecting, url={}, appId={}, resourceId={}, appKey={}, accessTokenPresent={}",
                    properties.getWebsocketUrl(),
                    properties.getAppId(),
                    properties.getResourceId(),
                    properties.getAppKey(),
                    !isBlank(properties.getAccessToken()));
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                    .build();
            // requestId 同时放到 X-Api-Request-Id 和 X-Api-Connect-Id，方便火山侧定位这一次连接。
            String requestId = UUID.randomUUID().toString();
            WebSocket.Builder builder = client.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                    .header("X-Api-App-ID", properties.getAppId())
                    .header("X-Api-Access-Key", properties.getAccessToken())
                    .header("X-Api-Resource-Id", properties.getResourceId())
                    .header("X-Api-App-Key", properties.getAppKey())
                    .header("X-Api-Connect-Id", requestId);
            final String currentVoiceSessionId = voiceSessionId;
            WebSocket webSocket = builder.buildAsync(URI.create(properties.getWebsocketUrl()), new WebSocket.Listener() {
                /*
                 * JDK WebSocket 的 onBinary 可能分多次回调同一条 WebSocket 消息。
                 *
                 * last=false 表示这条消息还没结束，只是其中一个片段；
                 * last=true 表示这条消息收完整了。
                 *
                 * 所以这里先把片段攒到 binaryBuffer，等 last=true 后再按火山协议解析完整帧。
                 */
                private final ByteArrayOutputStream binaryBuffer = new ByteArrayOutputStream();

                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    // 正常情况下火山主要返回二进制协议帧。
                    // 这里保留 onText，是为了兼容火山可能返回的文本错误信息或调试事件。
                    log.info("doubao realtime voice text event, voiceSessionId={}, data={}", currentVoiceSessionId, data);
                    DoubaoRealtimeVoiceEventListener listener = listenerMap.get(currentVoiceSessionId);
                    if (listener != null) {
                        listener.onEvent(currentVoiceSessionId, String.valueOf(data));
                    }
                    // JDK WebSocket 有背压机制。处理完当前消息后，要 request(1) 才会继续推下一条消息。
                    webSocket.request(1);
                    return null;
                }

                @Override
                public CompletionStage<?> onBinary(WebSocket webSocket, java.nio.ByteBuffer data, boolean last) {
                    // 先收集当前二进制片段。如果 last=false，说明这条 WebSocket 消息还没收完整。
                    appendBinaryFragment(data);
                    if (!last) {
                        // 告诉 JDK：当前片段处理完了，可以继续给我下一个片段或下一条消息。
                        webSocket.request(1);
                        return null;
                    }
                    // last=true 时，binaryBuffer 里才是一条完整的火山协议帧。
                    byte[] frameBytes = binaryBuffer.toByteArray();
                    binaryBuffer.reset();
                    JSONObject serverEvent = parseServerFrame(ByteBuffer.wrap(frameBytes));
                    log.info("doubao realtime voice binary event, voiceSessionId={}, serverSessionId={}, event={}, eventType={}, messageType={}, flags={}, serialization={}, payloadBytes={}, audioBytes={}, audioHeadHex={}, payload={}",
                            currentVoiceSessionId,
                            serverEvent.getString("sessionId"),
                            serverEvent.getInteger("event"),
                            serverEvent.getString("eventType"),
                            serverEvent.getInteger("messageType"),
                            serverEvent.getInteger("flags"),
                            serverEvent.getInteger("serialization"),
                            serverEvent.getInteger("payloadBytes"),
                            serverEvent.getInteger("audioBytes"),
                            serverEvent.getString("audioHeadHex"),
                            limit(serverEvent.getString("payload"), 300));
                    // 如果当前帧是 event=50 或 event=150，就唤醒 start() 里等待的 CompletableFuture。
                    completeStartupEvent(currentVoiceSessionId, serverEvent);
                    DoubaoRealtimeVoiceEventListener listener = listenerMap.get(currentVoiceSessionId);
                    if (listener != null) {
                        // 把火山事件交给 MockInterviewVoiceWebSocketHandler，再由它转发给前端浏览器。
                        listener.onEvent(currentVoiceSessionId, serverEvent.toJSONString());
                    }
                    // 继续接收下一条火山 WebSocket 消息。没有这句，后续事件可能不再进入 onBinary/onText。
                    webSocket.request(1);
                    return null;
                }

                private void appendBinaryFragment(ByteBuffer data) {
                    // data 是 JDK 传进来的 ByteBuffer，读取会改变 position。
                    // 使用只读副本，避免影响底层缓冲区状态。
                    ByteBuffer copy = data.asReadOnlyBuffer();
                    byte[] bytes = new byte[copy.remaining()];
                    copy.get(bytes);
                    binaryBuffer.write(bytes, 0, bytes.length);
                }
            }).join();
            // buildAsync(...).join() 成功，只表示 WebSocket 握手成功。
            // 还不能认为语音会话可用，后面还要等待 StartConnection 和 StartSession 的成功事件。
            webSocketMap.put(voiceSessionId, webSocket);
            CompletableFuture<JSONObject> connectionReady = new CompletableFuture<>();
            CompletableFuture<JSONObject> sessionReady = new CompletableFuture<>();

            connectionReadyMap.put(voiceSessionId, connectionReady);
            sessionReadyMap.put(voiceSessionId, sessionReady);


            // 第一步：告诉火山“我要开始一条连接”。
            sendJsonEvent(webSocket, EVENT_START_CONNECTION, null, new JSONObject());
            // 等火山返回 event=50。没有确认就继续发 StartSession，容易出现时序问题。
            awaitStartupEvent(connectionReady, voiceSessionId, "StartConnection");


            // 第二步：开始一个具体语音会话，并配置面试官提示词、ASR 输入格式、TTS 输出格式。
            sendJsonEvent(webSocket, EVENT_START_SESSION, voiceSessionId, buildStartSessionPayload(prompt));
            // 等火山返回 event=150。收到后，才认为这条火山语音会话真正 ready。
            awaitStartupEvent(sessionReady, voiceSessionId, "StartSession");
            return buildConnectedRes(voiceSessionId, interviewSessionId, prompt);
        } catch (Exception e) {
            // 启动过程中任何一步失败，都要清理已经创建的 WebSocket 和 Future，避免残留连接。
            close(voiceSessionId);
            WebSocketHandshakeException handshakeException = findHandshakeException(e);
            if (handshakeException != null) {
                throw BusinessException.RemoteError.newInstance("火山实时语音连接失败：" + buildHandshakeError(handshakeException));
            }
            throw BusinessException.RemoteError.newInstance("火山实时语音连接失败：" + e.getMessage());
        }
    }

    private WebSocketHandshakeException findHandshakeException(Throwable throwable) {
        // buildAsync().join() 抛出的异常经常会被 CompletionException 包一层。
        // 这里向内层查找 WebSocketHandshakeException，方便把 HTTP 状态码和响应头打印出来。
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof WebSocketHandshakeException exception) {
                return exception;
            }
            current = current instanceof CompletionException ? current.getCause() : current.getCause();
        }
        return null;
    }

    private String buildHandshakeError(WebSocketHandshakeException e) {
        // 握手失败时，火山可能通过 HTTP 状态码和响应头说明原因，例如鉴权失败、资源 ID 错误等。
        HttpResponse<?> response = e.getResponse();
        if (response == null) {
            return e.getMessage();
        }
        return "握手失败，HTTP状态码=" + response.statusCode()
                + "，headers=" + response.headers().map()
                + "，body=" + response.body();
    }

    public void registerListener(String voiceSessionId, DoubaoRealtimeVoiceEventListener listener) {
        // 前端 WebSocket 建立后，会注册 listener。
        // 后续火山返回事件时，就能通过这个 listener 发回对应浏览器。
        if (!isBlank(voiceSessionId) && listener != null) {
            listenerMap.put(voiceSessionId, listener);
        }
    }

    public void unregisterListener(String voiceSessionId) {
        // 前端 WebSocket 断开时取消监听，避免火山后续事件继续写已经关闭的浏览器连接。
        if (!isBlank(voiceSessionId)) {
            listenerMap.remove(voiceSessionId);
        }
    }

    public boolean forwardAudio(String voiceSessionId, byte[] audioBytes) {
        /*
         * 前端浏览器采集到麦克风 PCM 后，会通过 Java WebSocket 发到 MockInterviewVoiceWebSocketHandler。
         * Handler 再调用这里，把 PCM 包成火山协议里的 TaskRequest 音频帧。
         */
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            return false;
        }
        if (isBlank(voiceSessionId) || audioBytes == null || audioBytes.length == 0) {
            return false;
        }
        WebSocket webSocket = webSocketMap.get(voiceSessionId);
        if (webSocket == null) {
            return false;
        }
        try {
            sendAudioEvent(webSocket, voiceSessionId, audioBytes);
            return true;
        } catch (Exception e) {
            log.warn("doubao realtime voice forward audio failed, voiceSessionId={}", voiceSessionId, e);
            return false;
        }
    }

    /**
     * 浏览器语音通道建立后，让 AI 主动播报配置的开场白。
     * 必须等浏览器监听器注册完成后再发送，否则火山返回的首段音频可能无人接收。
     */
    public boolean sayHello(String voiceSessionId) {
        if (isBlank(voiceSessionId) || isBlank(properties.getHelloContent())) {
            return false;
        }
        WebSocket webSocket = webSocketMap.get(voiceSessionId);
        if (webSocket == null) {
            return false;
        }
        try {
            JSONObject payload = new JSONObject();
            payload.put("content", properties.getHelloContent().trim());
            sendJsonEvent(webSocket, EVENT_SAY_HELLO, voiceSessionId, payload);
            return true;
        } catch (Exception e) {
            log.warn("doubao realtime voice say hello failed, voiceSessionId={}", voiceSessionId, e);
            return false;
        }
    }

    public boolean commitAudio(String voiceSessionId) {
        /*
         * AUDIO_COMMIT 的含义是：用户这一轮回答已经说完。
         *
         * 火山协议里，发送一个 payload 为空的 audio frame，用来通知服务端当前音频段结束。
         * 服务端收到后，才会开始更明确地输出 ASR 结果、模型回答和 TTS 音频。
         */
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            return false;
        }
        WebSocket webSocket = webSocketMap.get(voiceSessionId);
        if (webSocket == null) {
            return false;
        }
        try {
            log.info("doubao realtime voice commit audio, voiceSessionId={}", voiceSessionId);
            webSocket.sendBinary(ByteBuffer.wrap(buildAudioFrame(voiceSessionId, new byte[0])), true);
            return true;
        } catch (Exception e) {
            log.warn("doubao realtime voice commit audio failed, voiceSessionId={}", voiceSessionId, e);
            return false;
        }
    }

    public void close(String voiceSessionId) {
        // 关闭顺序：先从本地 Map 移除，避免新音频继续写入；再通知火山结束 session/connection。
        if (isBlank(voiceSessionId)) {
            return;
        }
        WebSocket webSocket = webSocketMap.remove(voiceSessionId);
        listenerMap.remove(voiceSessionId);
        connectionReadyMap.remove(voiceSessionId);
        sessionReadyMap.remove(voiceSessionId);
        if (webSocket != null) {
            try {
                // 尽量按火山协议补发结束事件，再关闭底层 WebSocket。
                sendJsonEvent(webSocket, EVENT_FINISH_SESSION, voiceSessionId, new JSONObject());
                sendJsonEvent(webSocket, EVENT_FINISH_CONNECTION, null, new JSONObject());
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "mock interview voice closed");
            } catch (Exception ignored) {
                // 关闭阶段失败不再向外抛，避免影响用户退出页面流程。
            }
        }
    }

    private void completeStartupEvent(String voiceSessionId, JSONObject serverEvent) {
        // onBinary 是异步回调；start() 正在 awaitStartupEvent 里同步等待。
        // 收到对应 event 后，通过 completeFuture 唤醒等待中的 start()。
        Integer event = serverEvent.getInteger("event");
        if (event == null) {
            return;
        }
        if (event == EVENT_CONNECTION_STARTED) {
            completeFuture(connectionReadyMap.get(voiceSessionId), serverEvent);
        } else if (event == EVENT_SESSION_STARTED) {
            completeFuture(sessionReadyMap.get(voiceSessionId), serverEvent);
        }
    }

    private void completeFuture(CompletableFuture<JSONObject> future, JSONObject serverEvent) {
        // 防止重复 event 造成重复 complete。CompletableFuture 只能完成一次。
        if (future != null && !future.isDone()) {
            future.complete(serverEvent);
        }
    }

    private void awaitStartupEvent(CompletableFuture<JSONObject> future, String voiceSessionId, String eventName) throws Exception {
        // 把火山异步事件变成同步等待。超时后会抛异常，由 start() 统一清理连接并返回错误。
        if (future == null) {
            throw new IllegalStateException(eventName + " 等待器未初始化");
        }
        long timeoutSeconds = Math.max(3L, properties.getConnectTimeoutSeconds());
        JSONObject event = future.get(timeoutSeconds, TimeUnit.SECONDS);
        log.info("doubao realtime voice {} acknowledged, voiceSessionId={}, serverSessionId={}, event={}",
                eventName,
                voiceSessionId,
                event.getString("sessionId"),
                event.getInteger("event"));
    }

    private JSONObject buildStartSessionPayload(String prompt) {
        /*
         * StartSession 是最关键的请求。
         *
         * dialog：告诉模型它是谁、要怎么面试、系统提示词是什么。
         * asr.audio_info：告诉火山，学生上传进来的麦克风音频是什么格式。
         * tts.audio_config：告诉火山，AI 面试官返回的声音要用什么格式。
         */
        JSONObject payload = new JSONObject();
        JSONObject dialog = new JSONObject();
        dialog.put("bot_name", "模拟面试官");
        dialog.put("system_role", prompt);
        dialog.put("speaking_style", "自然、专业、简洁，一次只问一个问题");
        JSONObject dialogExtra = new JSONObject();
        // 端到端模型版本放在 dialog.extra.model。当前 1.2.1.1 对应火山文档里的 O2.0 版本。
        dialogExtra.put("model", properties.getModelVersion());
        dialog.put("extra", dialogExtra);

        // 学生输入音频配置：前端发上来的是 16k、单声道、pcm_s16le。
        JSONObject asr = new JSONObject();
        JSONObject audioInfo = new JSONObject();
        audioInfo.put("format", properties.getInputAudioFormat());
        audioInfo.put("sample_rate", properties.getInputAudioSampleRate());
        audioInfo.put("channel", properties.getInputAudioChannel());
        asr.put("audio_info", audioInfo);

        // 面试官输出音频配置：要求火山直接返回 PCM，方便浏览器用 WebAudio 播放。
        JSONObject tts = new JSONObject();
        tts.put("speaker", properties.getSpeaker());
        tts.put("audio_config", buildTtsAudioConfigPayload());

        payload.put("dialog", dialog);
        payload.put("asr", asr);
        payload.put("tts", tts);
        return payload;
    }

    private JSONObject buildTtsAudioConfigPayload() {
        // 输出给前端播放的 AI 面试官音频格式。前端播放代码要和这里保持一致。
        JSONObject audioConfig = new JSONObject();
        audioConfig.put("channel", 1);
        audioConfig.put("format", properties.getOutputAudioFormat());
        audioConfig.put("sample_rate", properties.getOutputAudioSampleRate());
        return audioConfig;
    }

    private byte[] buildJsonFrame(int event, String sessionId, JSONObject payload) throws IOException {
        // JSON 事件帧：messageType=0x01，serialization=0x01 表示 payload 是 JSON。
        return buildFrame(0x01, 0x01, event, sessionId, payload.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    private byte[] buildAudioFrame(String sessionId, byte[] audioBytes) throws IOException {
        // 音频帧：messageType=0x02，serialization=0x00 表示 payload 是原始音频字节。
        return buildFrame(0x02, 0x00, EVENT_TASK_REQUEST, sessionId, audioBytes);
    }

    private void sendJsonEvent(WebSocket webSocket, int event, String sessionId, JSONObject payload) throws IOException {
        // 火山控制事件虽然内容是 JSON，但外层仍然要包成火山二进制协议帧发送。
        byte[] frame = buildJsonFrame(event, sessionId, payload);
        log.info("doubao realtime voice send json event, event={}, sessionId={}, payload={}",
                event,
                sessionId,
                limit(payload.toJSONString(), 300));
        webSocket.sendBinary(ByteBuffer.wrap(frame), true);
    }

    private void sendAudioEvent(WebSocket webSocket, String sessionId, byte[] audioBytes) throws IOException {
        // 前端传来的 PCM 不做 base64，不包 JSON，直接放进音频协议帧。
        byte[] frame = buildAudioFrame(sessionId, audioBytes);
        webSocket.sendBinary(ByteBuffer.wrap(frame), true);
    }

    private byte[] buildFrame(int messageType, int serialization, int event, String sessionId, byte[] payload) throws IOException {
        /*
         * 火山二进制帧大致结构：
         *
         * 1. 协议头 4 字节
         * 2. event 编号
         * 3. 可选 sessionId
         * 4. payload 长度
         * 5. payload 内容
         *
         * 注意：这里的整数按大端序写入，所以使用 writeInt 手动写 4 个字节。
         */
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // 0x11：高 4 位是协议版本，低 4 位是 header size。这里 header size=1，表示 4 字节基础头。
        output.write(0x11);
        // 高 4 位 messageType；低 4 位 flags。
        // 0x04 表示这条协议帧携带 event。sessionId 是否携带由 event 是否属于 Session 级事件决定。
        output.write((messageType << 4) | 0x04);
        // 高 4 位 serialization；低 4 位 compression。当前不压缩，所以低 4 位为 0。
        output.write(serialization << 4);
        // reserved byte。
        output.write(0x00);
        writeInt(output, event);
        if (sessionId != null) {
            byte[] sessionBytes = sessionId.getBytes(StandardCharsets.UTF_8);
            writeInt(output, sessionBytes.length);
            output.write(sessionBytes);
        }
        writeInt(output, payload.length);
        output.write(payload);
        return output.toByteArray();
    }

    private JSONObject parseServerFrame(ByteBuffer data) {
        /*
         * 解析火山返回的二进制协议帧。
         *
         * 这里最终会转成一个统一 JSON，方便上层 Handler 判断：
         * - 如果有 audio 字段，说明这是火山返回的 PCM 音频
         * - 如果有 text/payload/json 字段，说明这是文本事件
         * - event/eventType 用来区分 StartConnection 成功、StartSession 成功、服务端回复等
         */
        ByteBuffer buffer = data.asReadOnlyBuffer().order(ByteOrder.BIG_ENDIAN);
        JSONObject result = new JSONObject();
        if (buffer.remaining() < 4) {
            result.put("type", "server.frame.invalid");
            return result;
        }
        int header = buffer.get() & 0xFF;
        int typeAndFlags = buffer.get() & 0xFF;
        int serializationAndCompression = buffer.get() & 0xFF;
        buffer.get();

        int messageType = (typeAndFlags >> 4) & 0x0F;
        int flags = typeAndFlags & 0x0F;
        int serialization = (serializationAndCompression >> 4) & 0x0F;

        // ERROR_INFORMATION(0x0F) 的布局不是普通事件帧：
        // 4 字节错误码 + 4 字节 payload 长度 + JSON payload。
        // 如果继续按 event/sessionId 解析，错误码会被混入文本，浏览器就会显示二进制乱码。
        if (messageType == 0x0F) {
            Integer errorCode = buffer.remaining() >= 4 ? buffer.getInt() : null;
            byte[] errorPayload = readPayload(buffer);
            String errorText = new String(errorPayload, StandardCharsets.UTF_8);
            result.put("type", "SERVER_ERROR");
            result.put("eventType", "SERVER_ERROR");
            result.put("messageType", messageType);
            result.put("flags", flags);
            result.put("serialization", serialization);
            result.put("errorCode", errorCode);
            result.put("payloadBytes", errorPayload.length);
            result.put("payload", errorText);
            try {
                JSONObject json = JSONObject.parseObject(errorText);
                result.put("json", json);
                result.put("text", firstString(json, "error", "message", "detail", "text"));
            } catch (Exception ignored) {
                result.put("text", errorText);
            }
            return result;
        }

        Integer event = null;
        String sessionId = null;
        Integer sequence = null;
        if ((flags & 0x04) != 0 && buffer.remaining() >= 4) {
            // flags 包含 0x04 时，后面会带 event 编号。
            event = buffer.getInt();
        }
        if (hasSequence(flags) && buffer.remaining() >= 4) {
            // 部分服务端帧可能带 sequence，用于有序消息或分片场景。
            sequence = buffer.getInt();
        }
        if (isSessionLevelEvent(event) && buffer.remaining() >= 4) {
            // Session 级事件会携带 sessionId；Connection 级事件通常不携带 sessionId。
            String parsedSessionId = readOptionalSessionId(buffer, true);
            if (!isBlank(parsedSessionId)) {
                sessionId = parsedSessionId;
            }
        }
        // payload 可能是 JSON 文本，也可能是原始 PCM 音频。
        byte[] payload = readPayload(buffer);

        result.put("event", event);
        result.put("eventType", event == null ? null : "SERVER_EVENT_" + event);
        result.put("sessionId", sessionId);
        result.put("messageType", messageType);
        result.put("flags", flags);
        result.put("serialization", serialization);
        result.put("sequence", sequence);
        result.put("payloadBytes", payload.length);
        if (payload.length > 0 && event != null && (event == EVENT_SERVER_AUDIO_ONLY_RESPONSE || serialization == 0)) {
            // 音频 payload 是二进制，不能直接塞成字符串。
            // 这里暂时放 base64 到 JSON，之后 Handler 会解码后用 BinaryMessage 发给前端。
            result.put("audio", Base64.getEncoder().encodeToString(payload));
            result.put("audioBytes", payload.length);
            result.put("audioHeadHex", toHex(payload, 16));
        } else if (payload.length > 0) {
            // 非音频 payload 按 UTF-8 文本处理；如果 serialization=1，再尝试解析成 JSON。
            String text = new String(payload, StandardCharsets.UTF_8);
            result.put("payload", text);
            if (serialization == 1) {
                try {
                    JSONObject json = JSONObject.parseObject(text);
                    result.put("json", json);
                    result.put("text", firstString(json, "text", "content", "delta", "transcript"));
                } catch (Exception ignored) {
                    result.put("text", text);
                }
            } else {
                result.put("text", text);
            }
        }
        if (event != null && event == EVENT_SERVER_FULL_RESPONSE) {
            result.put("eventType", "SERVER_FULL_RESPONSE");
        } else if (event != null && event == EVENT_SERVER_AUDIO_ONLY_RESPONSE) {
            result.put("eventType", "SERVER_AUDIO_ONLY_RESPONSE");
        }
        return result;
    }

    private boolean hasSequence(int flags) {
        // flags 低两位和 sequence 是否存在有关。
        int sequenceFlag = flags & 0x03;
        return sequenceFlag == 0x01 || sequenceFlag == 0x03;
    }

    private boolean isSessionLevelEvent(Integer event) {
        if (event == null) {
            return false;
        }
        return event != EVENT_START_CONNECTION
                && event != EVENT_FINISH_CONNECTION
                && event != EVENT_CONNECTION_STARTED;
    }

    private String readOptionalSessionId(ByteBuffer buffer, boolean requiredByEvent) {
        /*
         * sessionId 的格式是：4 字节长度 + sessionId 字节。
         *
         * 但不是每个帧都有 sessionId。Connection 级事件通常不带 sessionId；
         * Session 级事件才会携带 sessionId。
         * 这里先 mark，尝试读取；如果不像 sessionId，就 reset 回原位置。
         */
        buffer.mark();
        int sessionSize = buffer.getInt();
        if (sessionSize > 0 && sessionSize <= buffer.remaining()) {
            byte[] sessionBytes = new byte[sessionSize];
            buffer.get(sessionBytes);
            String sessionId = new String(sessionBytes, StandardCharsets.UTF_8);
            if (isLikelySessionId(sessionId) || requiredByEvent) {
                return sessionId;
            }
        }
        buffer.reset();
        return null;
    }

    private boolean isLikelySessionId(String value) {
        // 我们自己生成的 voiceSessionId 是 UUID，火山 sessionId 通常也是可打印的短标识。
        // 如果读出来包含奇怪字符，大概率不是 sessionId，而是 payload 的一部分。
        if (isBlank(value) || value.length() > 128) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            boolean allowed = (ch >= '0' && ch <= '9')
                    || (ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || ch == '-'
                    || ch == '_';
            if (!allowed) {
                return false;
            }
        }
        return true;
    }

    private byte[] readPayload(ByteBuffer buffer) {
        /*
         * payload 一般是：4 字节长度 + payload 内容。
         * 如果长度字段不合理，就退回并把剩余内容当作 payload，增强兼容性。
         */
        if (!buffer.hasRemaining()) {
            return new byte[0];
        }
        if (buffer.remaining() >= 4) {
            buffer.mark();
            int payloadSize = buffer.getInt();
            if (payloadSize >= 0 && payloadSize <= buffer.remaining()) {
                byte[] payload = new byte[payloadSize];
                buffer.get(payload);
                return payload;
            }
            buffer.reset();
        }
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);
        return payload;
    }

    private String toHex(byte[] bytes, int limit) {
        // 只打印音频前几个字节，方便判断是不是 PCM，同时避免日志过大。
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int length = Math.min(bytes.length, limit);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(String.format("%02x", bytes[i] & 0xFF));
        }
        return builder.toString();
    }

    private void writeInt(ByteArrayOutputStream output, int value) {
        // 火山协议里的 int 使用大端序：高位字节在前。
        output.write((value >> 24) & 0xFF);
        output.write((value >> 16) & 0xFF);
        output.write((value >> 8) & 0xFF);
        output.write(value & 0xFF);
    }

    private String firstString(JSONObject object, String... keys) {
        // 不同火山事件里的文本字段名称可能不同，这里按常见字段顺序取第一个非空值。
        for (String key : keys) {
            String value = object.getString(key);
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private StudentMockInterviewVoiceStartRes buildLocalReadyRes(String voiceSessionId, Long interviewSessionId, String prompt) {
        StudentMockInterviewVoiceStartRes res = new StudentMockInterviewVoiceStartRes();
        res.setVoiceSessionId(voiceSessionId);
        res.setInterviewSessionId(interviewSessionId);
        res.setStatus("LOCAL_READY");
        res.setProvider(PROVIDER);
        res.setConnected(false);
        res.setMessage("实时语音配置未启用，已完成本地语音会话准备");
        res.setPromptPreview(limit(prompt, 500));
        return res;
    }

    private StudentMockInterviewVoiceStartRes buildConnectedRes(String voiceSessionId, Long interviewSessionId, String prompt) {
        StudentMockInterviewVoiceStartRes res = new StudentMockInterviewVoiceStartRes();
        res.setVoiceSessionId(voiceSessionId);
        res.setInterviewSessionId(interviewSessionId);
        res.setStatus("CONNECTED");
        res.setProvider(PROVIDER);
        res.setConnected(true);
        res.setMessage("火山实时语音 WebSocket 已建立");
        res.setPromptPreview(limit(prompt, 500));
        return res;
    }

    private String limit(String value, int limit) {
        if (value == null || value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
