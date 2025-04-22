package org.dromara.common.satoken.core.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.dromara.common.redis.utils.RedisUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token持久层接口(使用框架自带RedisUtils实现 协议统一)
 * <p>
 * 采用 caffeine + redis 多级缓存 优化并发查询效率
 *
 * @author Lion Li
 */
public class PlusSaTokenDao implements SaTokenDao {

    private static final Cache<String, Object> CAFFEINE = Caffeine.newBuilder()
        // 设置最后一次写入或访问后经过固定时间过期
        .expireAfterWrite(5, TimeUnit.SECONDS)
        // 初始的缓存空间大小
        .initialCapacity(100)
        // 缓存的最大条数
        .maximumSize(65535)
        .build();
    private static final Cache<String, Boolean> CAFFEINE_WRITE = Caffeine.newBuilder()
        // 设置最后一次写入或访问后经过固定时间过期
        .expireAfterWrite(10, TimeUnit.SECONDS)
        // 初始的缓存空间大小
        .initialCapacity(100)
        // 缓存的最大条数
        .maximumSize(65535)
        .build();

    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        Object o = CAFFEINE.get(key, k -> RedisUtils.getObject(key));
        return (String) o;
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }
        // 如果本地缓存未过期，则不做任何处理
        if (CAFFEINE_WRITE.getIfPresent(key) != null) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == NEVER_EXPIRE) {
            RedisUtils.setObject(key, value);
        } else {
            if (RedisUtils.hasKey(key)) {
                RedisUtils.setObject(key, value, true);
            } else {
                RedisUtils.setObject(key, value, Duration.ofSeconds(timeout));
            }
        }
        CAFFEINE.invalidate(key);
        CAFFEINE_WRITE.put(key, Boolean.TRUE);
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setObject(key, value, true);
            CAFFEINE.invalidate(key);
            CAFFEINE_WRITE.invalidate(key);
        }
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        RedisUtils.deleteObject(key);
        CAFFEINE.invalidate(key);
        CAFFEINE_WRITE.invalidate(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        long timeout = RedisUtils.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout));
        CAFFEINE.invalidate(key);
        CAFFEINE_WRITE.invalidate(key);
    }

    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return CAFFEINE.get(key, k -> RedisUtils.getObject(key));
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == NEVER_EXPIRE) {
            RedisUtils.setObject(key, object);
        } else {
            if (RedisUtils.hasKey(key)) {
                RedisUtils.setObject(key, object, true);
            } else {
                RedisUtils.setObject(key, object, Duration.ofSeconds(timeout));
            }
        }
        CAFFEINE.invalidate(key);
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setObject(key, object, true);
            CAFFEINE.invalidate(key);
            CAFFEINE_WRITE.invalidate(key);
        }
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        RedisUtils.deleteObject(key);
        CAFFEINE.invalidate(key);
        CAFFEINE_WRITE.invalidate(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        long timeout = RedisUtils.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout));
        CAFFEINE.invalidate(key);
        CAFFEINE_WRITE.invalidate(key);
    }


    /**
     * 搜索数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        String keyStr = prefix + "*" + keyword + "*";
        return (List<String>) CAFFEINE.get(keyStr, k -> {
            Collection<String> keys = RedisUtils.keys(keyStr);
            List<String> list = new ArrayList<>(keys);
            return SaFoxUtil.searchList(list, start, size, sortType);
        });
    }
}
