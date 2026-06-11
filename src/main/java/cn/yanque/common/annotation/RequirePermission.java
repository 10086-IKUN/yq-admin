package cn.yanque.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 可标注在Controller类或方法上，方法上的注解优先级高于类上的
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限编码，如 "user:add", "role:update"
     * 多个权限用逗号分隔，如 "user:add,user:update"
     */
    String value();

    /**
     * 多个权限时的逻辑关系
     * true: 需要所有权限（AND）
     * false: 只需要其中一个权限（OR，默认）
     */
    boolean logicalAnd() default false;
}
