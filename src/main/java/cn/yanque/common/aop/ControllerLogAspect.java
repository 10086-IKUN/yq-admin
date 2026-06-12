package cn.yanque.common.aop;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器日志切面
 * 自动记录所有Controller方法的请求参数、响应结果和耗时
 * 对敏感字段（password、secret、token）自动脱敏
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    private static final int MAX_LOG_LENGTH = 4000;

    /**
     * 环绕通知：记录接口请求开始、结束和耗时
     * @param joinPoint 连接点
     * @return 方法执行结果
     */
    @Around("execution(* cn.yanque..controller..*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
        HttpServletRequest request = getCurrentRequest();

        log.info("接口开始: uri={}, httpMethod={}, controller={}, args={}",
                request == null ? "-" : request.getRequestURI(),
                request == null ? "-" : request.getMethod(),
                methodName,
                buildArgsLog(joinPoint.getArgs(), signature.getParameterNames()));

        Object result = joinPoint.proceed();
        long cost = System.currentTimeMillis() - start;
        log.info("接口结束: uri={}, controller={}, cost={}ms, result={}",
                request == null ? "-" : request.getRequestURI(),
                methodName,
                cost,
                toJson(sanitizeObject(result)));
        return result;
    }



    /**
     * 获取当前请求对象
     * @return HttpServletRequest，无上下文时返回null
     */
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 构建请求参数日志（过滤掉请求/响应和文件参数）
     * @param args 参数数组
     * @param parameterNames 参数名数组
     * @return JSON格式参数日志
     */
    private String buildArgsLog(Object[] args, String[] parameterNames) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (shouldIgnore(arg)) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", parameterNames != null && i < parameterNames.length ? parameterNames[i] : "arg" + i);
            item.put("value", sanitizeObject(arg));
            items.add(item);
        }
        return toJson(items);
    }

    /**
     * 判断是否应忽略该参数（请求/响应/文件类型）
     * @param arg 参数对象
     * @return true表示应忽略
     */
    private boolean shouldIgnore(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof MultipartFile;
    }

    /**
     * 清洗对象中的敏感信息，递归处理集合和嵌套对象
     * @param value 原始值
     * @return 清洗后的值
     */
    private Object sanitizeObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof MultipartFile file) {
            return Map.of(
                    "fileName", file.getOriginalFilename(),
                    "size", file.getSize()
            );
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean || value.getClass().isEnum()) {
            return value;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(sanitizeObject(Array.get(value, i)));
            }
            return list;
        }
        if (value instanceof Iterable<?> iterable) {
            List<Object> list = new ArrayList<>();
            for (Object item : iterable) {
                list.add(sanitizeObject(item));
            }
            return list;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> sanitized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                sanitized.put(key, isSensitiveField(key) ? mask(String.valueOf(entry.getValue())) : sanitizeObject(entry.getValue()));
            }
            return sanitized;
        }
        if (shouldUseDirectString(value.getClass())) {
            return String.valueOf(value);
        }

        Map<String, Object> sanitized = new LinkedHashMap<>();
        Class<?> current = value.getClass();
        while (current != null && current != Object.class) {
            Field[] fields = current.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    sanitized.put(field.getName(), isSensitiveField(field.getName()) ? mask(fieldValue == null ? null : String.valueOf(fieldValue)) : sanitizeObject(fieldValue));
                } catch (Exception ignored) {
                    sanitized.put(field.getName(), "[unavailable]");
                }
            }
            current = current.getSuperclass();
        }
        return sanitized;
    }

    /**
     * 判断是否为JDK/Spring内置类（使用toString而非反射）
     * @param clazz 类对象
     * @return true表示使用直接字符串转换
     */
    private boolean shouldUseDirectString(Class<?> clazz) {
        Package currentPackage = clazz.getPackage();
        String packageName = currentPackage == null ? "" : currentPackage.getName();
        return packageName.startsWith("java.")
                || packageName.startsWith("javax.")
                || packageName.startsWith("jakarta.")
                || packageName.startsWith("org.springframework.");
    }

    /**
     * 判断字段名是否包含敏感关键词
     * @param fieldName 字段名
     * @return true表示敏感字段
     */
    private boolean isSensitiveField(String fieldName) {
        String lower = fieldName.toLowerCase();
        return lower.contains("password")
                || lower.contains("secret")
                || lower.contains("token");
    }

    /**
     * 脱敏处理：保留前2位和后2位，中间用****替代
     * @param value 原始值
     * @return 脱敏后的值
     */
    private String mask(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() <= 6) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }

    /**
     * 对象转JSON字符串
     * @param value 对象
     * @return JSON字符串
     */
    private String toJson(Object value) {
        try {
            return truncate(JSON.toJSONString(value));
        } catch (Exception ex) {
            return truncate(String.valueOf(value));
        }
    }

    /**
     * 截断过长的日志内容
     * @param value 原始字符串
     * @return 截断后的字符串（最大4000字符）
     */
    private String truncate(String value) {
        if (value == null || value.length() <= MAX_LOG_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LOG_LENGTH) + "...(truncated)";
    }
}
