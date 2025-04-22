package org.dromara.common.mybatis.helper;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;

import java.util.Map;

/**
 * InterceptorIgnoreHelper的替代类，目的是解决多处忽略策略共存的问题。
 * 使用此处理时，请勿使用InterceptorIgnoreHelper的静态方法，而是使用本类提供的方法，以免出现数据不同步问题。
 *
 * @author hexm
 * @date 2024/9/4
 */
public class MyInterceptorIgnoreHelper extends InterceptorIgnoreHelper {

    public static final ThreadLocal<Boolean> ignoreTenantLine = new InheritableThreadLocal<>();
    public static final ThreadLocal<Boolean> ignoreDynamicTableName = new InheritableThreadLocal<>();
    public static final ThreadLocal<Boolean> ignoreBlockAttack = new InheritableThreadLocal<>();
    public static final ThreadLocal<Boolean> ignoreIllegalSql = new InheritableThreadLocal<>();
    public static final ThreadLocal<Boolean> ignoreDataPermission = new InheritableThreadLocal<>();
    public static final ThreadLocal<Map<String, Boolean>> ignoreOthersByKey = new InheritableThreadLocal<>();

    /**
     * 设置忽略租户策略
     */
    public static void setIgnoreTenantLine() {
        if (ignoreTenantLine.get() == null || !ignoreTenantLine.get()) {
            ignoreTenantLine.set(true);
            resetStrategy();
        }
    }

    /**
     * 设置忽略表名策略
     */
    public static void setIgnoreDynamicTableName() {
        if (ignoreDynamicTableName.get() == null || !ignoreDynamicTableName.get()) {
            ignoreDynamicTableName.set(true);
            resetStrategy();
        }
    }

    /**
     * 设置忽略防注入策略
     */
    public static void setIgnoreBlockAttack() {
        if (ignoreBlockAttack.get() == null || !ignoreBlockAttack.get()) {
            ignoreBlockAttack.set(true);
            resetStrategy();
        }
    }

    /**
     * 设置忽略非法SQL策略
     */
    public static void setIgnoreIllegalSql() {
        if (ignoreIllegalSql.get() == null || !ignoreIllegalSql.get()) {
            ignoreIllegalSql.set(true);
            resetStrategy();
        }
    }

    /**
     * 设置忽略数据权限策略
     */
    public static void setIgnoreDataPermission() {
        if (ignoreDataPermission.get() == null || !ignoreDataPermission.get()) {
            ignoreDataPermission.set(true);
            resetStrategy();
        }
    }

    /**
     * 设置忽略其他策略
     *
     * @param ignore
     */
    public static void setIgnoreOthersByKey(Map<String, Boolean> ignore) {
        ignoreOthersByKey.set(ignore);
        resetStrategy();
    }

    public static void removeIgnoreTenantLine() {
        ignoreTenantLine.remove();
        resetStrategy();
    }

    public static void removeIgnoreDynamicTableName() {
        ignoreDynamicTableName.remove();
        resetStrategy();
    }

    public static void removeIgnoreBlockAttack() {
        ignoreBlockAttack.remove();
        resetStrategy();
    }

    public static void removeIgnoreIllegalSql() {
        ignoreIllegalSql.remove();
        resetStrategy();
    }

    public static void removeIgnoreDataPermission() {
        ignoreDataPermission.remove();
        resetStrategy();
    }

    public static void removeIgnoreOthersByKey() {
        ignoreOthersByKey.remove();
        resetStrategy();
    }

    /**
     * 清除忽略策略
     */
    public static void clearIgnoreStrategy() {
        ignoreTenantLine.remove();
        ignoreDynamicTableName.remove();
        ignoreBlockAttack.remove();
        ignoreIllegalSql.remove();
        ignoreDataPermission.remove();
        ignoreOthersByKey.remove();
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }

    /**
     * 构建忽略策略
     * <p>
     * 根据配置的忽略选项构建一个IgnoreStrategy实例，用于后续处理过程中决定是否忽略某些操作
     * 如果所有的忽略选项都未配置（即都为null），则返回null，表示没有特别的忽略策略需要执行
     *
     * @return IgnoreStrategy实例，或null（如果没有配置任何忽略选项）
     */
    public static IgnoreStrategy buildIgnoreStrategy() {
        // 获取各个忽略选项的配置，这里使用的是线程局部变量存储的配置
        Boolean tenantLine = ignoreTenantLine.get();
        Boolean dynamicTableName = ignoreDynamicTableName.get();
        Boolean blockAttack = ignoreBlockAttack.get();
        Boolean illegalSql = ignoreIllegalSql.get();
        Boolean dataPermission = ignoreDataPermission.get();
        Map<String, Boolean> othersByKey = ignoreOthersByKey.get();

        // 检查是否所有忽略选项都未配置，如果是，则返回null
        if (tenantLine == null && dynamicTableName == null && blockAttack == null && illegalSql == null && dataPermission == null && othersByKey == null) {
            return null;
        }

        // 使用Builder模式构建IgnoreStrategy实例，并根据配置设置各个忽略选项
        return IgnoreStrategy.builder()
            .tenantLine(tenantLine)
            .dynamicTableName(dynamicTableName)
            .blockAttack(blockAttack)
            .illegalSql(illegalSql)
            .dataPermission(dataPermission)
            .others(othersByKey)
            .build();
    }

    /**
     * 重置忽略策略
     * 该方法用于根据当前的忽略策略生成配置，重新初始化忽略策略
     * 如果当前的忽略策略为null，则清除现有的忽略策略；
     * 否则，使用当前的忽略策略进行处理
     */
    private static void resetStrategy() {
        // 获取当前构建的忽略策略
        IgnoreStrategy ignoreStrategy = buildIgnoreStrategy();
        // 如果忽略策略为null，则清除现有的忽略策略
        if (ignoreStrategy == null) {
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        } else {
            // 如果忽略策略不为null，则处理当前的忽略策略
            InterceptorIgnoreHelper.handle(ignoreStrategy);
        }
    }

}
