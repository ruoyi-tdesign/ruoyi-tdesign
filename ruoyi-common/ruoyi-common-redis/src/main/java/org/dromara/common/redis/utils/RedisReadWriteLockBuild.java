package org.dromara.common.redis.utils;

import lombok.Builder;
import lombok.Getter;
import org.dromara.common.core.constant.GlobalConstants;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * redis读写锁建造者
 *
 * @author hexm
 * @date 2023/05/17 10:54
 */
@Getter
@Builder
public class RedisReadWriteLockBuild {

    /**
     * 缓存key
     */
    private String key;

    /**
     * 过期时间
     */
    private Duration expire;

    /**
     * 是否重写过期时间
     */
    private boolean rewriteExpire;

    /**
     * 是否使用递增时间，true:使用{@link #incrementExpire} false:使用{@link #expire}
     * <br />
     * 只有在{@link #rewriteExpire}为true时生效.
     */
    private boolean useIncrement;

    /**
     * 每次读取时，递增时间.默认值为{@link #expire}
     */
    private Duration incrementExpire;

    /**
     * 最大过期时间.只有在{@link #incrementExpire}为true时生效.
     */
    private Duration maxExpire;

    /**
     * 同时执行读与写操作，当读取值不存在时，执行默认回调获取值
     *
     * @param defaultSupplier 默认值回调
     * @param <T>             数据类型
     * @param after           执行默认回调获取值后的操作，例如存入缓存中
     * @return 返回缓存的数据，如果不存在则在执行回调后返回回调值
     */
    public <T> T readWrite(Supplier<T> defaultSupplier, BiConsumer<String, T> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        boolean unlock = false;
        try {
            T data = RedisUtils.getObject(key);
            if (data == null && defaultSupplier != null) {
                RLock writeLock = readWriteLock.writeLock();
                // 此处读锁需要先释放才能获取写锁
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
                unlock = true;
                writeLock.lock();
                try {
                    data = RedisUtils.getObject(key);
                    // 双重检查
                    if (data == null) {
                        data = defaultSupplier.get();
                        after.accept(key, data);
                    }
                } finally {
                    if (writeLock.isHeldByCurrentThread()) {
                        writeLock.unlock();
                    }
                }
            } else if (rewriteExpire && expire != null) {
                setExpire();
            }
            return data;
        } finally {
            // 判断是否已经提前释放锁，避免锁重入时被提前释放
            if (!unlock) {
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
            }
        }
    }

    /**
     * 同时执行读与写操作，当读取值不存在时，执行默认回调获取值
     *
     * @param defaultSupplier 默认值回调
     * @param <T>             数据类型
     * @param after           执行默认回调获取值后的操作，例如存入缓存中
     * @return 返回缓存的数据，如果不存在则在执行回调后返回回调值
     */
    public <T> List<T> readWriteList(Supplier<List<T>> defaultSupplier, BiConsumer<String, List<T>> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        boolean unlock = false;
        try {
            Boolean hasKey = RedisUtils.hasKey(key);
            if (hasKey) {
                if (rewriteExpire && expire != null) {
                    setExpire();
                }
                return RedisUtils.getList(key);
            }
            List<T> data = null;
            if (defaultSupplier != null) {
                RLock writeLock = readWriteLock.writeLock();
                // 此处读锁需要先释放才能获取写锁
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
                unlock = true;
                writeLock.lock();
                try {
                    // 双重检查
                    hasKey = RedisUtils.hasKey(key);
                    if (hasKey) {
                        return RedisUtils.getList(key);
                    }
                    data = defaultSupplier.get();
                    after.accept(key, data);
                } finally {
                    writeLock.unlock();
                }
            }
            return data;
        } finally {
            // 判断是否已经提前释放锁，避免锁重入时被提前释放
            if (!unlock) {
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
            }
        }
    }

    /**
     * 同时执行读与写操作，当读取值不存在时，执行默认回调获取值
     *
     * @param defaultSupplier 默认值回调
     * @param <T>             数据类型
     * @param after           执行默认回调获取值后的操作，例如存入缓存中
     * @return 返回缓存的数据，如果不存在则在执行回调后返回回调值
     */
    public <T> Set<T> readWriteSet(Supplier<Set<T>> defaultSupplier, BiConsumer<String, Set<T>> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        boolean unlock = false;
        try {
            Boolean hasKey = RedisUtils.hasKey(key);
            if (hasKey) {
                if (rewriteExpire && expire != null) {
                    setExpire();
                }
                return RedisUtils.getSet(key);
            }
            Set<T> data = null;
            if (defaultSupplier != null) {
                RLock writeLock = readWriteLock.writeLock();
                // 此处读锁需要先释放才能获取写锁
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
                unlock = true;
                writeLock.lock();
                try {
                    // 双重检查
                    hasKey = RedisUtils.hasKey(key);
                    if (hasKey) {
                        return RedisUtils.getSet(key);
                    }
                    data = defaultSupplier.get();
                    after.accept(key, data);
                } finally {
                    writeLock.unlock();
                }
            }
            return data;
        } finally {
            // 判断是否已经提前释放锁，避免锁重入时被提前释放
            if (!unlock) {
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
            }
        }
    }

    /**
     * 同时执行读与写操作，当读取值不存在时，执行默认回调获取值
     *
     * @param defaultSupplier 默认值回调
     * @param <T>             数据类型
     * @param after           执行默认回调获取值后的操作，例如存入缓存中
     * @return 返回缓存的数据，如果不存在则在执行回调后返回回调值
     */
    public <T> Map<String, T> readWriteMap(Supplier<Map<String, T>> defaultSupplier, BiConsumer<String, Map<String, T>> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        boolean unlock = false;
        try {
            Boolean hasKey = RedisUtils.hasKey(key);
            if (hasKey) {
                if (rewriteExpire && expire != null) {
                    setExpire();
                }
                return RedisUtils.getMap(key);
            }
            Map<String, T> data = null;
            if (defaultSupplier != null) {
                RLock writeLock = readWriteLock.writeLock();
                // 此处读锁需要先释放才能获取写锁
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
                unlock = true;
                writeLock.lock();
                try {
                    // 双重检查
                    hasKey = RedisUtils.hasKey(key);
                    if (hasKey) {
                        return RedisUtils.getMap(key);
                    }
                    data = defaultSupplier.get();
                    after.accept(key, data);
                } finally {
                    writeLock.unlock();
                }
            }
            return data;
        } finally {
            // 判断是否已经提前释放锁，避免锁重入时被提前释放
            if (!unlock) {
                if (readLock.isHeldByCurrentThread()) {
                    readLock.unlock();
                }
            }
        }
    }

    /**
     * 执行读操作
     *
     * @return
     */
    public <T> T read(Function<String, T> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            T apply = after.apply(key);
            if (apply != null && rewriteExpire && expire != null) {
                setExpire();
            }
            return apply;
        } finally {
            if (readLock.isHeldByCurrentThread()) {
                readLock.unlock();
            }
        }
    }

    /**
     * 执行写操作
     *
     * @param defaultSupplier 获取值回调
     * @param after           执行回调获取值后的操作，例如存入缓存中
     * @return
     */
    public <T> T write(Supplier<T> defaultSupplier, BiConsumer<String, T> after) {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            T data = defaultSupplier.get();
            after.accept(key, data);
            return data;
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
    }

    /**
     * 执行删除操作
     *
     * @return
     */
    public void remove() {
        RReadWriteLock readWriteLock = RedisUtils.getClient().getReadWriteLock(getKey(key));
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
           RedisUtils.deleteObject(key);
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
    }

    /**
     * 设置过期时间
     */
    private void setExpire() {
        if (useIncrement) {
            long time = RedisUtils.getTimeToLive(key);
            if (time > 0) {
                RedisUtils.expire(key, Duration.ofMillis(Math.min(time + incrementExpire.toMillis(), getMaxExpire().toMillis())));
            }
        } else {
            RedisUtils.expire(key, expire);
        }
    }

    private static String getKey(String key) {
        return GlobalConstants.GLOBAL_REDIS_KEY + "readWriteLock:" + key;
    }

    public Duration getMaxExpire() {
        return maxExpire == null ? expire : maxExpire;
    }
}
