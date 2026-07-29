package org.dromara.common.redis.utils;

import org.dromara.common.core.constant.GlobalConstants;
import org.redisson.api.RLock;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * redis锁封装工具
 *
 * @author hexm
 * @date 2023/03/04 17:49
 */
public class RedisLockUtil {

    /**
     * 获取并保存到缓存中
     *
     * @param key             缓存key
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> T getOrSave(String key, Supplier<T> defaultSupplier) {
        return getOrAfter(key, null, defaultSupplier, RedisUtils::setObject);
    }

    /**
     * 获取并保存到缓存中，设置获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> T getOrSave(String key, Duration lease, Supplier<T> defaultSupplier) {
        return getOrAfter(key, lease, defaultSupplier, RedisUtils::setObject);
    }

    /**
     * 获取并保存到缓存中，设置获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param expire          过期时间
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> T getOrSave(String key, Duration lease, Duration expire, Supplier<T> defaultSupplier) {
        return getOrAfter(key, lease, defaultSupplier, (keyS, data) -> RedisUtils.setObject(keyS, data, expire));
    }

    /**
     * 获取并保存到缓存中
     *
     * @param key             缓存key
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> List<T> getOrSaveList(String key, Supplier<List<T>> defaultSupplier) {
        return getListOrAfter(key, null, defaultSupplier, RedisUtils::setList);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> List<T> getOrSaveList(String key, Duration lease, Supplier<List<T>> defaultSupplier) {
        return getListOrAfter(key, lease, defaultSupplier, RedisUtils::setList);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param expire          过期时间
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> List<T> getOrSaveList(String key, Duration lease, Duration expire, Supplier<List<T>> defaultSupplier) {
        return getListOrAfter(key, lease, defaultSupplier, (keyS, data) -> RedisUtils.setList(keyS, data, expire));
    }

    /**
     * 获取并保存到缓存中
     *
     * @param key             缓存key
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Set<T> getOrSaveSet(String key, Supplier<Set<T>> defaultSupplier) {
        return getSetOrAfter(key, null, defaultSupplier, RedisUtils::setSet);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Set<T> getOrSaveSet(String key, Duration lease, Supplier<Set<T>> defaultSupplier) {
        return getSetOrAfter(key, lease, defaultSupplier, RedisUtils::setSet);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param expire          过期时间
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Set<T> getOrSaveSet(String key, Duration lease, Duration expire, Supplier<Set<T>> defaultSupplier) {
        return getSetOrAfter(key, lease, defaultSupplier, (keyS, data) -> RedisUtils.setSet(keyS, data, expire));
    }

    /**
     * 获取并保存到缓存中
     *
     * @param key             缓存key
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Map<String, T> getOrSaveMap(String key, Supplier<Map<String, T>> defaultSupplier) {
        return getMapOrAfter(key, null, defaultSupplier, RedisUtils::setMap);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Map<String, T> getOrSaveMap(String key, Duration lease, Supplier<Map<String, T>> defaultSupplier) {
        return getMapOrAfter(key, lease, defaultSupplier, RedisUtils::setMap);
    }

    /**
     * 获取并保存到缓存中，设置指定获取锁后保留锁的最长时间
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param expire          过期时间
     * @param defaultSupplier 不存在值时回调
     */
    public static <T> Map<String, T> getOrSaveMap(String key, Duration lease, Duration expire, Supplier<Map<String, T>> defaultSupplier) {
        return getMapOrAfter(key, lease, defaultSupplier, (keyS, map) -> RedisUtils.setMap(keyS, map, expire));
    }

    /**
     * 获取缓存时，如果没有缓存时执行回调，并执行一个随后的操作
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     * @param after           没有缓存时执行的回调
     */
    public static <T> T getOrAfter(String key, Duration lease, Supplier<T> defaultSupplier, BiConsumer<String, T> after) {
        T data = RedisUtils.getObject(key);
        if (data == null) {
            RLock lock = getLock(key);
            if (lease == null || lease.getSeconds() < 0) {
                lock.lock();
            } else {
                lock.lock(lease.getSeconds(), TimeUnit.SECONDS);
            }
            try {
                data = RedisUtils.getObject(key);
                if (data == null) {
                    data = defaultSupplier.get();
                    after.accept(key, data);
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return data;
    }

    /**
     * 获取缓存时，如果没有缓存时执行回调，并执行一个随后的操作
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     * @param after           没有缓存时执行的回调
     */
    public static <T> List<T> getListOrAfter(String key, Duration lease, Supplier<List<T>> defaultSupplier, BiConsumer<String, List<T>> after) {
        Boolean hasKey = RedisUtils.hasKey(key);
        if (hasKey) {
            return RedisUtils.getList(key);
        } else {
            List<T> data;
            RLock lock = getLock(key);
            if (lease == null || lease.getSeconds() < 0) {
                lock.lock();
            } else {
                lock.lock(lease.getSeconds(), TimeUnit.SECONDS);
            }
            try {
                hasKey = RedisUtils.hasKey(key);
                if (hasKey) {
                    return RedisUtils.getList(key);
                } else {
                    data = defaultSupplier.get();
                    after.accept(key, data);
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            return data;
        }
    }

    /**
     * 获取缓存时，如果没有缓存时执行回调，并执行一个随后的操作
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     * @param after           没有缓存时执行的回调
     */
    public static <T> Set<T> getSetOrAfter(String key, Duration lease, Supplier<Set<T>> defaultSupplier, BiConsumer<String, Set<T>> after) {
        Boolean hasKey = RedisUtils.hasKey(key);
        if (hasKey) {
            return RedisUtils.getSet(key);
        } else {
            Set<T> data;
            RLock lock = getLock(key);
            if (lease == null || lease.getSeconds() < 0) {
                lock.lock();
            } else {
                lock.lock(lease.getSeconds(), TimeUnit.SECONDS);
            }
            try {
                hasKey = RedisUtils.hasKey(key);
                if (hasKey) {
                    return RedisUtils.getSet(key);
                } else {
                    data = defaultSupplier.get();
                    after.accept(key, data);
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            return data;
        }
    }

    /**
     * 获取缓存时，如果没有缓存时执行回调，并执行一个随后的操作
     *
     * @param key             缓存key
     * @param lease           如果尚未通过调用unlock释放锁，则在获取锁后保留锁的最长时间。如果lease为-1，则保持锁定直到显式解锁
     * @param defaultSupplier 不存在值时回调
     * @param after           没有缓存时执行的回调
     */
    public static <T> Map<String, T> getMapOrAfter(String key, Duration lease, Supplier<Map<String, T>> defaultSupplier, BiConsumer<String, Map<String, T>> after) {
        Boolean hasKey = RedisUtils.hasKey(key);
        if (hasKey) {
            return RedisUtils.getMap(key);
        } else {
            Map<String, T> data;
            RLock lock = getLock(key);
            if (lease == null || lease.getSeconds() < 0) {
                lock.lock();
            } else {
                lock.lock(lease.getSeconds(), TimeUnit.SECONDS);
            }
            try {
                hasKey = RedisUtils.hasKey(key);
                if (hasKey) {
                    return RedisUtils.getMap(key);
                } else {
                    data = defaultSupplier.get();
                    after.accept(key, data);
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            return data;
        }
    }

    /**
     * 在锁中执行过程
     *
     * @param key      锁key
     * @param runnable 运行过程
     */
    public static void execute(String key, Runnable runnable) {
        RLock lock = getLock(key);
        lock.lock();
        try {
            runnable.run();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 在锁中执行过程
     *
     * @param key      锁key
     * @param runnable 运行过程
     */
    public static <T> T execute(String key, Supplier<T> runnable) {
        RLock lock = getLock(key);
        lock.lock();
        try {
            return runnable.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取锁
     *
     * @param key 缓存key
     * @return
     */
    public static RLock getLock(String key) {
        return RedisUtils.getClient().getLock(getKey(key));
    }

    /**
     * 获取key
     *
     * @param key 缓存key
     * @return
     */
    public static String getKey(String key) {
        return GlobalConstants.GLOBAL_REDIS_KEY + "lock:" + key;
    }
}
