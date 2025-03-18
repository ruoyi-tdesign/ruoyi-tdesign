package org.dromara.common.sensitive.annotation;

import cn.dev33.satoken.annotation.SaMode;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dromara.common.sensitive.core.SensitiveStrategy;
import org.dromara.common.sensitive.handler.SensitiveHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解
 *
 * @author zhujie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveHandler.class)
public @interface Sensitive {
    SensitiveStrategy strategy();

    /**
     * 角色，用户登录类型如果拥有角色，则不进行脱敏
     */
    String[] roleKey() default {};

    /**
     * 权限，用户登录类型如果拥有权限，则不进行脱敏
     */
    String[] perms() default {};

    /**
     * 验证模式：AND | OR，默认AND
     */
    SaMode mode() default SaMode.AND;
}
