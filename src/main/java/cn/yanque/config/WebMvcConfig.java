package cn.yanque.config;

import cn.yanque.models.auth.interceptor.JwtAuthInterceptor;
import cn.yanque.models.auth.interceptor.PermissionInterceptor;
import cn.yanque.models.auth.interceptor.SignInterceptor;
import cn.yanque.models.auth.interceptor.StudentPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 拦截器配置。
 *
 * <p>统一注册接口访问链路：JWT 认证先写入登录身份，签名校验再做防篡改和防重放，
 * 最后由管理端权限拦截器和学生端权限拦截器分别处理资源权限。</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;
    @Autowired
    private SignInterceptor signInterceptor;
    @Autowired
    private StudentPermissionInterceptor studentPermissionInterceptor;

    /**
     * 注册拦截器
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录接口和支付宝异步回调不带业务 token，需要从认证链路中排除。
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/api/student/pay/notify",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/login/**");
        // 签名校验依赖 JWT 拦截器写入的 userId/studentId。
        registry.addInterceptor(signInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/api/student/pay/notify",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/login/**");
        // 管理端权限拦截器内部会根据注解或路径判断是否需要放行。
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/api/student/pay/notify",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/login/**");
        registry.addInterceptor(studentPermissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/student/login",
                        "/api/sysUser/login",
                        "/api/student/pay/notify",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/login/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
