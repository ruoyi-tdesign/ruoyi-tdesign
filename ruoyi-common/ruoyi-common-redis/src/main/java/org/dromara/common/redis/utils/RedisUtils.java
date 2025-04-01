package org.dromara.common.redis.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.options.KeysScanOptions;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * redis 工具类
 *
 * @author Lion Li
 * @version 3.1.0 新增
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 限流
     *
     * @param key          限流key
     * @param rateType     限流类型
     * @param rate         速率
     * @param rateInterval 速率间隔，秒
     * @return -1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        return rateLimiter(key, rateType, rate, rateInterval, 0);
    }

    /**
     * 限流
     *
     * @param key          限流key
     * @param rateType     限流类型
     * @param rate         速率
     * @param rateInterval 速率间隔，秒
     * @param timeout      超时时间，秒
     * @return -1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval, int timeout) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, Duration.ofSeconds(rateInterval), Duration.ofSeconds(timeout));
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    /**
     * 获取客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 发布通道消息
     *
     * @param channelKey 通道key
     * @param msg        发送数据
     * @param consumer   自定义处理
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    /**
     * 发布消息到指定的频道
     *
     * @param channelKey 通道key
     * @param msg        发送数据
     */
    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
    }

    /**
     * 订阅通道接收消息
     *
     * @param channelKey 通道key
     * @param clazz      消息类型
     * @param consumer   自定义处理
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    /**
     * 订阅通道接收消息
     *
     * @param channelKey 通道key
     * @param clazz      消息类型
     * @param listener   自定义处理
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, MessageListener<? extends T> listener) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, listener);
    }

    /**
     * 取消订阅
     *
     * @param channelKey 通道key
     * @param listener   自定义处理
     */
    public static void unsubscribe(String channelKey, MessageListener<?> listener) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.removeListener(listener);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static <T> void setObject(final String key, final T value) {
        setObject(key, value, false);
    }

    /**
     * 缓存基本的对象，保留当前对象 TTL 有效期
     *
     * @param key       缓存的键值
     * @param value     缓存的值
     * @param isSaveTtl 是否保留TTL有效期(例如: set之前ttl剩余90 set之后还是为90)
     * @since Redis 6.X 以上使用 setAndKeepTTL 兼容 5.X 方案
     */
    public static <T> void setObject(final String key, final T value, final boolean isSaveTtl) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long timeToLive = bucket.remainTimeToLive();
                if (timeToLive == -1) {
                    setObject(key, value);
                } else {
                    setObject(key, value, Duration.ofMillis(timeToLive));
                }
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param duration 时间
     */
    public static <T> void setObject(final String key, final T value, final Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RBucketAsync<T> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        bucket.expireAsync(duration);
        batch.execute();
    }

    /**
     * 如果不存在则设置 并返回 true 如果存在则返回 false
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return set成功或失败
     */
    public static <T> boolean setObjectIfAbsent(final String key, final T value, final Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfAbsent(value, duration);
    }

    /**
     * 如果存在则设置 并返回 true 如果存在则返回 false
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return set成功或失败
     */
    public static <T> boolean setObjectIfExists(final String key, final T value, final Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfExists(value, duration);
    }

    /**
     * 注册对象监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addObjectListener(final String key, final ObjectListener listener) {
        RBucket<T> result = CLIENT.getBucket(key);
        result.addListener(listener);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * 设置有效时间
     *
     * @param key      Redis键
     * @param duration 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final Duration duration) {
        RBucket rBucket = CLIENT.getBucket(key);
        return rBucket.expire(duration);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getObject(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.get();
    }

    /**
     * 获得key剩余存活时间
     *
     * @param key 缓存键值
     * @return 剩余存活时间.以毫秒为单位的时间 -2 如果密钥不存在。 -1 如果密钥存在但没有关联的过期时间。
     */
    public static <T> long getTimeToLive(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存的键值
     */
    public static boolean deleteObject(final String key) {
        return CLIENT.getBucket(key).delete();
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     */
    public static void deleteObject(final Collection collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach(t -> {
            batch.getBucket(t.toString()).deleteAsync();
        });
        batch.execute();
    }

    /**
     * 检查缓存对象是否存在
     *
     * @param key 缓存的键值
     */
    public static boolean isExistsObject(final String key) {
        return CLIENT.getBucket(key).isExists();
    }

    /**
     * 添加缓存List数据到末尾
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> boolean addList(final String key, final List<T> dataList) {
        RList<T> rList = CLIENT.getList(key);
        return rList.addAll(dataList);
    }

    /**
     * 追加缓存List数据
     *
     * @param key  缓存的键值
     * @param data 待缓存的数据
     * @return 缓存的对象
     */
    public static <T> boolean addList(final String key, final T data) {
        RList<T> rList = CLIENT.getList(key);
        return rList.add(data);
    }

    /**
     * 设置缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> void setList(final String key, final List<T> dataList) {
        RBatch batch = CLIENT.createBatch();
        RListAsync<Object> batchList = batch.getList(key);
        batchList.deleteAsync();
        batchList.addAllAsync(dataList);
        batch.execute();
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @param expire   过期时间
     * @return 缓存的对象
     */
    public static <T> void setList(final String key, final List<T> dataList, final Duration expire) {
        RBatch batch = CLIENT.createBatch();
        RListAsync<Object> batchList = batch.getList(key);
        batchList.deleteAsync();
        batchList.addAllAsync(dataList);
        batchList.expireAsync(expire);
        batch.execute();
    }

    /**
     * 注册List监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addListListener(final String key, final ObjectListener listener) {
        RList<T> rList = CLIENT.getList(key);
        rList.addListener(listener);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getList(final String key) {
        RList<T> rList = CLIENT.getList(key);
        return rList.readAll();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> boolean addSet(final String key, final Set<T> dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.addAll(dataSet);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> void setSet(final String key, final Set<T> dataSet) {
        RBatch batch = CLIENT.createBatch();
        RSetAsync<Object> batchList = batch.getSet(key);
        batchList.deleteAsync();
        batchList.addAllAsync(dataSet);
        batch.execute();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @param expire  过期时间
     * @return 缓存数据的对象
     */
    public static <T> void setSet(final String key, final Set<T> dataSet, final Duration expire) {
        RBatch batch = CLIENT.createBatch();
        RSetAsync<Object> batchList = batch.getSet(key);
        batchList.deleteAsync();
        batchList.expireAsync(expire);
        batchList.addAllAsync(dataSet);
        batch.execute();
    }

    /**
     * 注册Set监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addSetListener(final String key, final ObjectListener listener) {
        RSet<T> rSet = CLIENT.getSet(key);
        rSet.addListener(listener);
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存的key
     * @return set对象
     */
    public static <T> Set<T> getSet(final String key) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.readAll();
    }

    /**
     * 缓存ZSet
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> int addZSet(final String key, final Map<T, Double> dataSet) {
        RScoredSortedSet<T> scoredSortedSet = CLIENT.getScoredSortedSet(key);
        return scoredSortedSet.addAll(dataSet);
    }

    /**
     * 缓存ZSet
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> void setZSet(final String key, final Map<T, Double> dataSet) {
        RBatch batch = CLIENT.createBatch();
        RScoredSortedSetAsync<T> scoredSortedSet = batch.getScoredSortedSet(key);
        scoredSortedSet.deleteAsync();
        scoredSortedSet.addAllAsync(dataSet);
        batch.execute();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @param expire  过期时间
     * @return 缓存数据的对象
     */
    public static <T> void setZSet(final String key, final Map<T, Double> dataSet, final Duration expire) {
        RBatch batch = CLIENT.createBatch();
        RScoredSortedSetAsync<T> scoredSortedSet = batch.getScoredSortedSet(key);
        scoredSortedSet.deleteAsync();
        scoredSortedSet.expireAsync(expire);
        scoredSortedSet.addAllAsync(dataSet);
        batch.execute();
    }

    /**
     * 注册ZSet监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addZSetListener(final String key, final ObjectListener listener) {
        RScoredSortedSet<T> scoredSortedSet = CLIENT.getScoredSortedSet(key);
        scoredSortedSet.addListener(listener);
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存的key
     * @return set对象
     */
    public static <T> Set<T> getZSet(final String key) {
        RScoredSortedSet<T> scoredSortedSet = CLIENT.getScoredSortedSet(key);
        Collection<T> all = scoredSortedSet.readAll();
        if (all instanceof Set) {
            return (Set<T>) all;
        }
        return new LinkedHashSet<>(all);
    }

    /**
     * put缓存Map
     *
     * @param key     缓存的键值
     * @param dataMap 缓存的数据
     */
    public static <T> void putAllMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = CLIENT.getMap(key);
            rMap.putAll(dataMap);
        }
    }

    /**
     * 清空后，缓存Map
     *
     * @param key     缓存的键值
     * @param dataMap 缓存的数据
     */
    public static <T> void setMap(final String key, final Map<String, T> dataMap) {
        RBatch batch = CLIENT.createBatch();
        batch.getBucket(key).deleteAsync();
        if (dataMap != null) {
            RMapAsync<String, T> map = batch.getMap(key);
            map.putAllAsync(dataMap);
        }
        batch.execute();
    }

    /**
     * 清空后，缓存Map
     *
     * @param key     缓存的键值
     * @param dataMap 缓存的数据
     */
    public static <T> void setMap(final String key, final Map<String, T> dataMap, final Duration expire) {
        RBatch batch = CLIENT.createBatch();
        batch.getBucket(key).deleteAsync();
        if (dataMap != null) {
            RMapAsync<String, T> map = batch.getMap(key);
            map.putAllAsync(dataMap);
            map.expireAsync(expire);
        }
        batch.execute();
    }

    /**
     * 注册Map监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addMapListener(final String key, final ObjectListener listener) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.addListener(listener);
    }

    /**
     * 获得缓存的Map
     *
     * @param key 缓存的键值
     * @return map对象
     */
    public static <T> Map<String, T> getMap(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.getAll(rMap.keySet());
    }

    /**
     * 获得缓存Map的key列表
     *
     * @param key 缓存的键值
     * @return key列表
     */
    public static <T> Set<String> getMapKeySet(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.keySet();
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static <T> void putMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.put(hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T getMap(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.get(hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T removeMapKey(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.remove(hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static long removeMapKeys(final String key, final String... hKey) {
        return CLIENT.getMap(key).fastRemove(hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public static <K, V> Map<K, V> getMultiMap(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = CLIENT.getMap(key);
        return rMap.getAll(hKeys);
    }

    /**
     * 设置原子值
     *
     * @param key   Redis键
     * @param value 值
     */
    public static void setAtomicValue(String key, long value) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        atomic.set(value);
    }

    /**
     * 获取原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long getAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.get();
    }

    /**
     * 递增原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long incrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    /**
     * 递减原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long decrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.decrementAndGet();
    }

    /**
     * 获得缓存的基本对象列表(全局匹配忽略租户 自行移除租户id)
     * <P>
     * limit-设置扫描的限制数量(默认为0,查询全部)
     * pattern-设置键的匹配模式(默认为null)
     * chunkSize-设置每次扫描的块大小(默认为0,本方法设置为1000)
     * type-设置键的类型(默认为null,查询全部类型)
     * </P>
     * @see KeysScanOptions
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(final String pattern) {
        return  keys(KeysScanOptions.defaults().pattern(pattern).chunkSize(1000));
    }

    /**
     * 通过扫描参数获取缓存的基本对象列表
     * @param keysScanOptions 扫描参数
     * <P>
     * limit-设置扫描的限制数量(默认为0,查询全部)
     * pattern-设置键的匹配模式(默认为null)
     * chunkSize-设置每次扫描的块大小(默认为0)
     * type-设置键的类型(默认为null,查询全部类型)
     * </P>
     * @see KeysScanOptions
     */
    public static Collection<String> keys(final KeysScanOptions keysScanOptions) {
        Stream<String> keysStream = CLIENT.getKeys().getKeysStream(keysScanOptions);
        return keysStream.collect(Collectors.toList());
    }

    /**
     * 删除缓存的基本对象列表(全局匹配忽略租户 自行拼接租户id)
     *
     * @param pattern 字符串前缀
     */
    public static void deleteKeys(final String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    /**
     * 检查redis中是否存在key
     *
     * @param key 键
     */
    public static Boolean hasKey(String... key) {
        RKeys rKeys = CLIENT.getKeys();
        return rKeys.countExists(key) > 0;
    }
}
