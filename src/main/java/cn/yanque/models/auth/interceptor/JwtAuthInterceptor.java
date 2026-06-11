package cn.yanque.models.auth.interceptor;

import cn.hutool.jwt.JWT;
import cn.yanque.common.dataConfig.service.SysConfig;
import cn.yanque.common.dataConfig.service.SysConfigService;
import cn.yanque.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private SysConfigService sysConfigService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final byte[] JWT_KEY = "1234".getBytes();
    private static final String USER_ID = "uid";
    private static final String EXPIRE_TIME = "expire_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(401, "未登录或Token缺失");
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        try {
            JWT jwt = JWT.of(token).setKey(sysConfigService.getConfig(SysConfig.jwtSecret).getBytes());
            if (!jwt.verify()) {
                throw new BusinessException(401, "Token无效或已过期");
            }

            Object userId = jwt.getPayload(USER_ID);
            Object expireTime = jwt.getPayload(EXPIRE_TIME);
            if (userId == null || expireTime == null) {
                throw new BusinessException(401, "Token无效或已过期");
            }

            long expireTimestamp = Long.parseLong(String.valueOf(expireTime));
            if (System.currentTimeMillis() > expireTimestamp) {
                throw new BusinessException(401, "Token无效或已过期");
            }

            request.setAttribute("userId", Long.parseLong(String.valueOf(userId)));
            MDC.put("userId", String.valueOf(userId));
            return true;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(401, "Token无效或已过期");
        }
    }
}





