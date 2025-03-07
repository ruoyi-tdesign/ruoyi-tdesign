package org.dromara.common.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限组注解，用于标记数据权限配置数组
 *
 * @author Lion Li
 * @version 3.5.0
 * @see org.dromara.common.mybatis.annotation.DataColumn
 * @see org.dromara.common.mybatis.enums.DataScopeType
 * @see org.dromara.common.mybatis.handler.PlusDataPermissionHandler
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 数据权限配置数组，用于指定数据权限的占位符关键字和替换值
     *
     * @return 数据权限配置数组
     */
    DataColumn[] value();

}
