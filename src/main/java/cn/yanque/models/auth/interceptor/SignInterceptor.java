package cn.yanque.models.auth.interceptor;

import cn.yanque.common.exception.BusinessException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component

/**
 * 请求签名拦截器。
 *
 * <p>校验 X-Timestamp、X-Nonce 和 X-Sign，使用 Redis 中的登录签名密钥计算 HMAC-SHA256，
 * 同时用 nonce 做短期防重放。</p>
 */
public class SignInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String USER_SECRET_KEY_PREFIX = "yanque:sign:secret:";
    private static final String STUDENT_SECRET_KEY_PREFIX = "yanque:student:sign:secret:";
    private static final String USER_NONCE_KEY_PREFIX = "yanque:sign:nonce:";
    private static final String STUDENT_NONCE_KEY_PREFIX = "yanque:student:sign:nonce:";
    private static final long TIMESTAMP_TOLERANCE = 300L;
    private static final String HMAC_SHA256 = "HmacSHA256";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // SSE 的 SseEmitter 在响应完成时会触发一次 ASYNC dispatch。
        // 这不是新的业务请求，不能再次消费 nonce，否则会把合法长连接误判为重放请求。
        if (request.getDispatcherType() == DispatcherType.ASYNC) {
            return true;
        }

        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");
        String sign = request.getHeader("X-Sign");
        if (timestamp == null || nonce == null || sign == null) {
            throw new BusinessException(401, "签名参数缺失");
        }

        long reqTime;
        try {
            reqTime = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new BusinessException(401, "时间戳格式错误");
        }
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - reqTime) > TIMESTAMP_TOLERANCE) {
            throw new BusinessException(401, "请求已过期");
        }

        // JwtAuthInterceptor 会根据 token 类型写入 userId 或 studentId。
        Object studentId = request.getAttribute("studentId");
        Object userId = request.getAttribute("userId");
        if (studentId == null && userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 管理端和学生端使用不同 Redis 前缀，避免相同 ID 的账号串用签名密钥。
        boolean studentRequest = studentId != null;
        String id = String.valueOf(studentRequest ? studentId : userId);
        String secretPrefix = studentRequest ? STUDENT_SECRET_KEY_PREFIX : USER_SECRET_KEY_PREFIX;
        String noncePrefix = studentRequest ? STUDENT_NONCE_KEY_PREFIX : USER_NONCE_KEY_PREFIX;

        String signSecret = stringRedisTemplate.opsForValue().get(secretPrefix + id);
        if (signSecret == null) {
            throw new BusinessException(401, "签名密钥不存在，请重新登录");
        }

        // nonce 只允许使用一次，用于防止签名请求被重放。
        String nonceKey = noncePrefix + id + ":" + nonce;
        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(nonceKey, "1", TIMESTAMP_TOLERANCE, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(absent)) {
            throw new BusinessException(401, "重复请求");
        }

        // 签名原文必须和前端保持一致：method + uri + queryString + timestamp + nonce。
        String method = request.getMethod();
        String uri = request.getServletPath();
        String queryString = request.getQueryString() != null ? request.getQueryString() : "";
        String plaintext = String.join("\n", method, uri, queryString, timestamp, nonce);
        String serverSign = hmacSha256(plaintext, signSecret);
        if (!serverSign.equalsIgnoreCase(sign)) {
            throw new BusinessException(401, "签名验证失败");
        }

        return true;
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException(500, "签名计算失败");
        }
    }
}
