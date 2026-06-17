package cn.yanque.models.auth.interceptor;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.system.permission.pojo.entity.SysPermissionEntity;
import cn.yanque.models.system.role.pojo.entity.SysRoleEntity;
import cn.yanque.models.system.user.pojo.info.UserInfo;
import cn.yanque.models.system.user.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验拦截器
 * 根据注解或路径校验用户权限，支持AND/OR逻辑
 * 优先级：@SkipPermission跳过 > @RequirePermission注解 > 路径匹配
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserService sysUserService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 系统管理员角色编码 */
    private static final String ADMIN_ROLE_CODE = "SUPER_ADMIN";

    /**
     * 校验用户权限
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return 权限校验通过返回true，否则抛出403异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理Controller方法
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (handlerMethod.getBeanType().getPackageName().contains(".models.studentFront.")) {
            return true;
        }

        // 检查是否有跳过权限校验的注解（方法优先，其次类）
        if (hasSkipPermissionAnnotation(handlerMethod)) {
            return true;
        }

        // 获取当前用户ID
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 获取用户信息（包含权限列表）
        UserInfo userInfo = sysUserService.getUserInfo(Long.parseLong(String.valueOf(userId)));

        // 系统管理员直接放行
        if (isAdmin(userInfo)) {
            return true;
        }

        List<SysPermissionEntity> userPermissions = userInfo.getSysPermissionEntities();

        // 1. 优先检查方法上的注解
        RequirePermission methodAnnotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (methodAnnotation != null) {
            return checkAnnotationPermission(methodAnnotation, userPermissions);
        }

        // 2. 检查类上的注解
        RequirePermission classAnnotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        if (classAnnotation != null) {
            return checkAnnotationPermission(classAnnotation, userPermissions);
        }

        // 3. 没有注解时，使用路径匹配
        return checkPathPermission(request.getRequestURI(), userPermissions);
    }

    /**
     * 检查是否有跳过权限校验的注解
     */
    private boolean hasSkipPermissionAnnotation(HandlerMethod handlerMethod) {
        // 检查方法上的注解
        if (handlerMethod.getMethodAnnotation(SkipPermission.class) != null) {
            return true;
        }
        // 检查类上的注解
        return handlerMethod.getBeanType().getAnnotation(SkipPermission.class) != null;
    }

    /**
     * 判断用户是否是系统管理员
     */
    private boolean isAdmin(UserInfo userInfo) {
        List<SysRoleEntity> roles = userInfo.getSysRoleEntities();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(role -> ADMIN_ROLE_CODE.equals(role.getRoleCode()));
    }

    /**
     * 校验注解权限
     */
    private boolean checkAnnotationPermission(RequirePermission annotation, List<SysPermissionEntity> userPermissions) {
        String permissionValue = annotation.value();
        boolean logicalAnd = annotation.logicalAnd();

        // 获取用户的所有权限编码
        Set<String> userPermCodes = userPermissions.stream()
                .map(SysPermissionEntity::getPermissionCode)
                .collect(Collectors.toSet());

        if (logicalAnd) {
            // 需要所有权限（AND）
            List<String> requiredPermissions = Arrays.asList(permissionValue.split(","));
            boolean hasAll = requiredPermissions.stream().allMatch(userPermCodes::contains);
            if (!hasAll) {
                throw new BusinessException(403, "无权限访问");
            }
        } else {
            // 只需要其中一个权限（OR）
            if (!userPermCodes.contains(permissionValue)) {
                throw new BusinessException(403, "无权限访问");
            }
        }

        return true;
    }

    /**
     * 校验路径权限（兜底逻辑）
     */
    private boolean checkPathPermission(String requestURI, List<SysPermissionEntity> userPermissions) {
        if (userPermissions.isEmpty()) {
            throw new BusinessException(403, "用户没有权限");
        }

        for (SysPermissionEntity permission : userPermissions) {
            String apiPath = permission.getApiPath();
            if (apiPath != null && pathMatcher.match(apiPath, requestURI)) {
                return true;
            }
        }

        throw new BusinessException(403, "无权限访问");
    }
}
