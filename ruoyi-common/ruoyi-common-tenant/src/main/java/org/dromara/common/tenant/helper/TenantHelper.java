package org.dromara.common.tenant.helper;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.convert.Convert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.service.TenantAppService;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.funtion.Apply;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.mybatis.helper.MyInterceptorIgnoreHelper;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.context.SaSecurityContext;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 租户助手
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantHelper {

    // 动态租户的缓存key
    private static final String DYNAMIC_TENANT_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "dynamicTenant";
    // 临时动态租户，可重入可动态切换
    private static final ThreadLocal<Deque<String>> TEMP_DYNAMIC_TENANT = InheritableThreadLocal.withInitial(ArrayDeque::new);
    // 忽略缓存
    private static final ThreadLocal<Boolean> IGNORE_CACHE_TENANT = new InheritableThreadLocal<>();
    // 忽略租户db重入计数,防止重入调用提前关闭租户
    private static final ThreadLocal<AtomicInteger> HEAVY_ENTRY_IGNORE_DB_TENANT = InheritableThreadLocal.withInitial(() -> new AtomicInteger(0));
    // 忽略租户缓存重入计数,防止重入调用提前关闭租户
    private static final ThreadLocal<AtomicInteger> HEAVY_ENTRY_IGNORE_CACHE_TENANT = InheritableThreadLocal.withInitial(() -> new AtomicInteger(0));

    /**
     * 是否启用了缓存忽略租户
     *
     * @return
     */
    public static boolean isIgnoreCache() {
        return Objects.equals(true, IGNORE_CACHE_TENANT.get());
    }

    /**
     * 租户功能是否启用
     */
    public static boolean isEnable() {
        return Convert.toBool(SpringUtils.getProperty("tenant.enable"), false);
    }

    /**
     * 开启忽略租户(开启后需手动调用 {@link #disableIgnore()} 关闭)
     */
    public static void enableIgnore() {
        MyInterceptorIgnoreHelper.setIgnoreTenantLine();
        HEAVY_ENTRY_IGNORE_DB_TENANT.get().incrementAndGet();
    }

    /**
     * 关闭忽略租户
     */
    public static void disableIgnore() {
        disableIgnore(false);
    }

    /**
     * 关闭忽略租户
     * <p>
     * 强制执行释放可能会造成逻辑错误，请谨慎使用
     *
     * @param force 强制执行
     */
    public static void disableIgnore(boolean force) {
        if (force || HEAVY_ENTRY_IGNORE_DB_TENANT.get().decrementAndGet() <= 0) {
            MyInterceptorIgnoreHelper.removeIgnoreTenantLine();
            HEAVY_ENTRY_IGNORE_DB_TENANT.remove();
        }
    }

    /**
     * 开启缓存忽略租户(开启后需手动调用 {@link #disableIgnore()} 关闭)
     * 效果与使用全局缓存一致
     */
    public static void enableIgnoreCache() {
        IGNORE_CACHE_TENANT.set(true);
        HEAVY_ENTRY_IGNORE_CACHE_TENANT.get().incrementAndGet();
    }

    /**
     * 关闭缓存忽略租户
     */
    public static void disableIgnoreCache() {
        disableIgnoreCache(false);
    }

    /**
     * 关闭缓存忽略租户
     * <p>
     * 强制执行释放可能会造成逻辑错误，请谨慎使用
     *
     * @param force 强制执行
     */
    public static void disableIgnoreCache(boolean force) {
        if (force || HEAVY_ENTRY_IGNORE_CACHE_TENANT.get().decrementAndGet() <= 0) {
            IGNORE_CACHE_TENANT.remove();
            HEAVY_ENTRY_IGNORE_CACHE_TENANT.remove();
        }
    }

    /**
     * 设置动态租户（当前执行线程）
     *
     * @param tenantId 租户id
     */
    public static void setDynamicTenant(String tenantId) {
        TEMP_DYNAMIC_TENANT.get().push(tenantId);
    }

    /**
     * 获取当前动态租户
     *
     * @return
     */
    public static String getDynamicTenant() {
        return TEMP_DYNAMIC_TENANT.get().peek();
    }

    /**
     * 移除动态租户
     */
    public static void removeDynamicTenant() {
        removeDynamicTenant(false);
    }

    /**
     * 移除动态租户
     *
     * @param force 是否强制
     */
    public static void removeDynamicTenant(boolean force) {
        TEMP_DYNAMIC_TENANT.get().poll();
        if (TEMP_DYNAMIC_TENANT.get().isEmpty() || force) {
            TEMP_DYNAMIC_TENANT.remove();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Apply handle) {
        enableIgnore();
        try {
            handle.apply();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param enabled 是否忽略租户
     * @param handle  处理执行方法
     */
    public static void ignore(boolean enabled, Apply handle) {
        if (enabled) {
            ignore(handle);
        } else {
            handle.apply();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Supplier<T> handle) {
        enableIgnore();
        try {
            return handle.get();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param enabled 是否确认
     * @param handle  处理执行方法
     */
    public static <T> T ignore(boolean enabled, Supplier<T> handle) {
        if (enabled) {
            return ignore(handle);
        }
        return handle.get();
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Consumer<String> handle) {
        String tenantId = getTenantId();
        enableIgnore();
        try {
            handle.accept(tenantId);
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param enabled 是否忽略租户
     * @param handle  处理执行方法
     */
    public static void ignore(boolean enabled, Consumer<String> handle) {
        if (enabled) {
            ignore(handle);
        } else {
            handle.accept(getTenantId());
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Function<String, T> handle) {
        String tenantId = getTenantId();
        enableIgnore();
        try {
            return handle.apply(tenantId);
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param enabled 是否忽略租户
     * @param handle  处理执行方法
     */
    public static <T> T ignore(boolean enabled, Function<String, T> handle) {
        if (enabled) {
            return ignore(handle);
        } else {
            return handle.apply(getTenantId());
        }
    }

    /**
     * 设置当前用户的动态租户(一直有效 需要手动清理)
     */
    public static void setUserDynamicTenant(String tenantId) {
        BaseUser user = SaSecurityContext.getContext();
        String cacheKey = DYNAMIC_TENANT_KEY + ":" + Objects.requireNonNull(user).getUserId();
        RedisUtils.setObject(cacheKey, tenantId);
        SaHolder.getStorage().set(cacheKey, tenantId);
    }

    /**
     * 获取当前用户的动态租户(一直有效 需要手动清理)
     */
    public static String getUserDynamicTenant() {
        BaseUser user = SaSecurityContext.getContext();
        String tenantId = null;
        if (user != null) {
            String cacheKey = DYNAMIC_TENANT_KEY + ":" + user.getUserId();
            tenantId = (String) SaHolder.getStorage().get(cacheKey);
            if (StringUtils.isNotBlank(tenantId)) {
                return tenantId;
            }
            tenantId = RedisUtils.getObject(cacheKey);
            if (StringUtils.isBlank(tenantId)) {
                return null;
            }
            SaHolder.getStorage().set(cacheKey, tenantId);
        }
        return tenantId;
    }

    /**
     * 清除当前用户的动态租户
     */
    public static void clearUserDynamicTenant() {
        BaseUser user = SaSecurityContext.getContext();
        String cacheKey = DYNAMIC_TENANT_KEY + ":" + Objects.requireNonNull(user).getUserId();
        RedisUtils.deleteObject(cacheKey);
        SaHolder.getStorage().delete(cacheKey);
    }

    /**
     * 在动态租户环境中执行(可重入)
     *
     * @param tenantId 租户id
     * @param handle   处理逻辑
     */
    public static void dynamicTenant(String tenantId, Apply handle) {
        setDynamicTenant(tenantId);
        try {
            handle.apply();
        } finally {
            removeDynamicTenant(false);
        }
    }

    /**
     * 在动态租户环境中执行并返回(可重入)
     *
     * @param tenantId 租户id
     * @param handle   处理逻辑
     */
    public static <T> T dynamicTenant(String tenantId, Supplier<T> handle) {
        setDynamicTenant(tenantId);
        try {
            return handle.get();
        } finally {
            removeDynamicTenant(false);
        }
    }

    /**
     * 当前是否处于动态租户环境
     *
     * @return
     */
    public static boolean isDynamic() {
        return getDynamicTenant() != null || getUserDynamicTenant() != null;
    }

    /**
     * 获取当前租户id(动态租户优先)
     */
    public static String getTenantId() {
        if (!isEnable()) {
            return null;
        }
        String tenantId = getDynamicTenant();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = TenantHelper.getUserDynamicTenant();
        }
        if (StringUtils.isBlank(tenantId)) {
            BaseUser user = SaSecurityContext.getContext();
            if (user != null) {
                tenantId = user.getTenantId();
            }
        }
        if (StringUtils.isBlank(tenantId)) {
            String appKey = ServletUtils.getAppKey();
            if (StringUtils.isNotBlank(appKey)) {
                TenantAppService service = SpringUtils.getBean(TenantAppService.class);
                tenantId = service.getTenantIdByAppKey(appKey);
            }
        }
        return tenantId;
    }

}
