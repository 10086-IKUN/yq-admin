package cn.yanque.models.auth.interceptor;

import cn.yanque.common.annotation.RequireStudentPermission;
import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import cn.yanque.models.studentFront.service.StudentFrontService;
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

@Component
public class StudentPermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private StudentFrontService studentFrontService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只处理 Controller 方法，非接口方法直接放过。
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        // 学生权限只作用于 studentFront 包，避免影响管理端学员管理接口。
        if (!handlerMethod.getBeanType().getPackageName().contains(".models.studentFront.")) {
            return true;
        }
        if (hasSkipPermissionAnnotation(handlerMethod)) {
            return true;
        }

        Object studentId = request.getAttribute("studentId");
        if (studentId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 权限来源：学生绑定的有效模板 -> 模板关联权限 -> stu_permission。
        List<StuPermissionEntity> permissions = studentFrontService.getPermissionsByStudentId(Long.parseLong(String.valueOf(studentId)));
        RequireStudentPermission methodAnnotation = handlerMethod.getMethodAnnotation(RequireStudentPermission.class);
        if (methodAnnotation != null) {
            return checkAnnotationPermission(methodAnnotation, permissions);
        }

        RequireStudentPermission classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireStudentPermission.class);
        if (classAnnotation != null) {
            return checkAnnotationPermission(classAnnotation, permissions);
        }

        return checkPathPermission(request.getServletPath(), permissions);
    }

    private boolean hasSkipPermissionAnnotation(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(SkipPermission.class) != null
                || handlerMethod.getBeanType().getAnnotation(SkipPermission.class) != null;
    }

    private boolean checkAnnotationPermission(RequireStudentPermission annotation, List<StuPermissionEntity> permissions) {
        // 注解权限优先，用于明确要求某个权限编码的接口。
        Set<String> codes = permissions.stream()
                .map(StuPermissionEntity::getPermissionCode)
                .collect(Collectors.toSet());
        List<String> required = Arrays.asList(annotation.value().split(","));
        boolean passed = annotation.logicalAnd()
                ? required.stream().allMatch(codes::contains)
                : required.stream().anyMatch(codes::contains);
        if (!passed) {
            throw new BusinessException(403, "无权限访问");
        }
        return true;
    }

    private boolean checkPathPermission(String path, List<StuPermissionEntity> permissions) {
        // 没有权限注解时，用 stu_permission.api_path 做兜底路径匹配。
        for (StuPermissionEntity permission : permissions) {
            String apiPath = permission.getApiPath();
            if (apiPath != null && !apiPath.isBlank() && pathMatcher.match(apiPath, path)) {
                return true;
            }
        }
        throw new BusinessException(403, "无权限访问");
    }
}
