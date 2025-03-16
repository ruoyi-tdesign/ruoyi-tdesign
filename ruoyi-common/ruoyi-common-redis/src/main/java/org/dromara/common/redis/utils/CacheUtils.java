package org.dromara.common.redis.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 缓存操作工具类
 *
 * @author Michelle.Chung
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked"})
public class CacheUtils {

    private static final CacheManager CACHE_MANAGER = SpringUtils.getBean(CacheManager.class);

    /**
     * 获取缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static <T> T get(String cacheNames, Object key) {
        Cache.ValueWrapper wrapper = CACHE_MANAGER.getCache(cacheNames).get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    /**
     * 获取缓存值, 如果不存在则获取值回调后重新保存
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     * @param cacheValue 缓存值
     */
    public static <T> T get(String cacheNames, Object key, Supplier<T> cacheValue) {
        Cache.ValueWrapper wrapper = CACHE_MANAGER.getCache(cacheNames).get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        T t = cacheValue.get();
        put(cacheNames, key, t);
        return t;
    }

    /**
     * 保存缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     * @param value      缓存值
     */
    public static void put(String cacheNames, Object key, Object value) {
        CACHE_MANAGER.getCache(cacheNames).put(key, value);
    }

    /**
     * 删除缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static void evict(String cacheNames, Object key) {
        CACHE_MANAGER.getCache(cacheNames).evict(key);
    }

    /**
     * 清空缓存值
     *
     * @param cacheNames 缓存组名称
     */
    public static void clear(String cacheNames) {
        CACHE_MANAGER.getCache(cacheNames).clear();
    }

    /**
     * 从缓存中拿值，如果不存在的话，则从回调中取值
     *
     * @param cacheNames 缓存组名称
     * @param keys       缓存keys
     * @param notPresent 获取不存在的值的回调
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<T> takeCache(String cacheNames, Collection<K> keys, Function<Set<K>, Map<K, T>> notPresent) {
        return StreamUtils.take(keys, k -> get(cacheNames, k), notPresent);
    }

    /**
     * 从缓存中拿值，如果不存在的话，则从回调中取值并将值放入缓存中
     *
     * @param cacheNames 缓存组名称
     * @param keys       缓存keys
     * @param notPresent 获取不存在的值的回调
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<T> takeCacheAndSet(String cacheNames, Collection<K> keys, Function<Set<K>, Map<K, T>> notPresent) {
        return StreamUtils.take(keys, k -> get(cacheNames, k), (k, t) -> put(cacheNames, k, t), notPresent);
    }
}
