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
 * Web MVC配置类
 * 配置拦截器、跨域等
 */
/**
 * Web MVC配置类
 * 注册拦截器链：JWT认证 → 签名校验 → 权限校验
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
        // JWT权限认证
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**");
        // 签名校验
        registry.addInterceptor(signInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**");
        // 权限校验
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/sysUser/login",
                        "/api/student/login",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**");
        registry.addInterceptor(studentPermissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/student/login",
                        "/api/sysUser/login",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
