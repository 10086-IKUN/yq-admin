package cn.yanque.models.studentFront.websocket;

import cn.hutool.jwt.JWT;
import cn.yanque.models.system.config.service.SysConfig;
import cn.yanque.models.system.config.service.SysConfigService;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.mockinterview.mapper.MockInterviewSessionMapper;
import cn.yanque.models.mockinterview.pojo.entity.MockInterviewSessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class MockInterviewVoiceHandshakeInterceptor implements HandshakeInterceptor {

    static final String ATTR_STUDENT_ID = "studentId";
    static final String ATTR_SESSION_ID = "sessionId";
    static final String ATTR_VOICE_SESSION_ID = "voiceSessionId";

    private static final String STUDENT_ID = "uid";
    private static final String USER_TYPE = "user_type";
    private static final String EXPIRE_TIME = "expire_time";

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private EduStudentMapper studentMapper;

    @Autowired
    private MockInterviewSessionMapper mockInterviewSessionMapper;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        Map<String, String> params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().toSingleValueMap();
        String token = params.get("token");
        String sessionIdText = params.get("sessionId");
        String voiceSessionId = params.get("voiceSessionId");
        if (isBlank(token) || isBlank(sessionIdText) || isBlank(voiceSessionId)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        Long studentId = parseStudentId(token);
        Long sessionId = parseLong(sessionIdText);
        if (studentId == null || sessionId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        MockInterviewSessionEntity session = mockInterviewSessionMapper.selectById(sessionId);
        if (session == null || !studentId.equals(session.getStudentId())) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        if (!"IN_PROGRESS".equals(session.getStatus()) || !voiceSessionId.equals(session.getVoiceSessionId())) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        attributes.put(ATTR_STUDENT_ID, studentId);
        attributes.put(ATTR_SESSION_ID, sessionId);
        attributes.put(ATTR_VOICE_SESSION_ID, voiceSessionId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private Long parseStudentId(String token) {
        try {
            JWT jwt = JWT.of(token).setKey(sysConfigService.getConfig(SysConfig.jwtSecret).getBytes());
            if (!jwt.verify()) {
                return null;
            }
            Object studentId = jwt.getPayload(STUDENT_ID);
            Object expireTime = jwt.getPayload(EXPIRE_TIME);
            if (studentId == null || expireTime == null || !"STUDENT".equals(String.valueOf(jwt.getPayload(USER_TYPE)))) {
                return null;
            }
            if (System.currentTimeMillis() > Long.parseLong(String.valueOf(expireTime))) {
                return null;
            }
            EduStudentEntity student = studentMapper.selectById(Long.parseLong(String.valueOf(studentId)));
            return student == null ? null : student.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
