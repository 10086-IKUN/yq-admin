package cn.yanque.models.auth.interceptor;

import cn.hutool.jwt.JWT;
import cn.yanque.models.system.config.service.SysConfig;
import cn.yanque.models.system.config.service.SysConfigService;
import cn.yanque.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器。
 *
 * <p>验证请求头中的 Bearer Token，并按 token 中的 user_type 把身份写入 request：
 * 管理端写 userId，学生端写 studentId，后续签名和权限拦截器依赖这两个属性区分账号体系。</p>
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private SysConfigService sysConfigService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID = "uid";
    private static final String EXPIRE_TIME = "expire_time";
    private static final String USER_TYPE = "user_type";
    private static final String STUDENT = "STUDENT";

    /**
     * 校验 JWT 并提取登录身份。
     */
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

            // 学生端和管理端共用 JWT 结构，但后续权限体系不同，所以这里写入不同的 request 属性。
            if (STUDENT.equals(String.valueOf(jwt.getPayload(USER_TYPE)))) {
                request.setAttribute("studentId", Long.parseLong(String.valueOf(userId)));
                MDC.put("studentId", String.valueOf(userId));
            } else {
                request.setAttribute("userId", Long.parseLong(String.valueOf(userId)));
                MDC.put("userId", String.valueOf(userId));
            }
            return true;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(401, "Token无效或已过期");
        }
    }
}





