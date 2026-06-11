package cn.yanque.models.auth.interceptor;

import cn.yanque.common.exception.BusinessException;
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
public class SignInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String SECRET_KEY_PREFIX = "yanque:sign:secret:";
    private static final String NONCE_KEY_PREFIX = "yanque:sign:nonce:";
    private static final long TIMESTAMP_TOLERANCE = 300L;
    private static final String HMAC_SHA256 = "HmacSHA256";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");
        String sign = request.getHeader("X-Sign");

        // 1. 参数完整性校验
        if (timestamp == null || nonce == null || sign == null) {
            throw new BusinessException(401, "签名参数缺失");
        }

        // 2. 时间戳校验
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

        // 3. 获取当前用户ID
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        String uid = String.valueOf(userId);

        // 4. 从Redis获取签名密钥
        String signSecret = stringRedisTemplate.opsForValue().get(SECRET_KEY_PREFIX + uid);
        if (signSecret == null) {
            throw new BusinessException(401, "签名密钥不存在，请重新登录");
        }

        // 5. 防重放校验
        String nonceKey = NONCE_KEY_PREFIX + uid + ":" + nonce;
        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(nonceKey, "1", TIMESTAMP_TOLERANCE, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(absent)) {
            throw new BusinessException(401, "重复请求");
        }

        // 6. 构建签名原文: method + uri + queryString + timestamp + nonce
        String method = request.getMethod();
        String uri = request.getServletPath();
        String queryString = request.getQueryString() != null ? request.getQueryString() : "";
        String plaintext = String.join("\n", method, uri, queryString, timestamp, nonce);

        // 7. 计算签名并比对
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
