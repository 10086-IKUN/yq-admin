package cn.yanque.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 学生端接口权限注解。
 *
 * <p>由 {@code StudentPermissionInterceptor} 读取，用于声明学生访问某个接口时必须具备的权限编码。</p>
 */
public @interface RequireStudentPermission {

    /**
     * 权限编码，多个编码用英文逗号分隔。
     */
    String value();

    /**
     * 多权限校验关系。
     *
     * <p>true 表示必须全部具备，false 表示满足任意一个即可。</p>
     */
    boolean logicalAnd() default false;
}
