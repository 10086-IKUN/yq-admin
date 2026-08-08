package cn.yanque.config;

import cn.yanque.models.studentFront.websocket.MockInterviewVoiceHandshakeInterceptor;
import cn.yanque.models.studentFront.websocket.MockInterviewVoiceWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MockInterviewVoiceWebSocketHandler mockInterviewVoiceWebSocketHandler;

    @Autowired
    private MockInterviewVoiceHandshakeInterceptor mockInterviewVoiceHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mockInterviewVoiceWebSocketHandler, "/api/student/mock-interview/voice/ws")
                .addInterceptors(mockInterviewVoiceHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
