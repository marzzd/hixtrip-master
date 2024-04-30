package com.hixtrip.sample.infra.utils;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 */
public class CacheUtil {

    private static RedisTemplate redisTemplate;


    private static class CacheSingle {
        private static final CacheUtil instance = new CacheUtil();
    }

    private CacheUtil() {
    }

    public static void init(RedisTemplate redisTemplate) {
        CacheSingle.instance.redisTemplate = redisTemplate;
    }

    public static RedisTemplate getRedisTemplate() {
        return CacheSingle.instance.redisTemplate;
    }


    /**
     * 判断key是否存在
     *
     * @param key 缓存的键值
     */
    public static boolean exist(final String key) {
        return getRedisTemplate().hasKey(key);
    }

    /**
     * 判断hash类型key中对应的hkey是否存在
     *
     * @param key 缓存的键值
     */
    public static boolean exist(final String key, final String hKey) {
        return getHashOperations().hasKey(key, hKey);
    }


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static <T> void setCacheObject(final String key, final T value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key     缓存的键值
     * @param value   缓存的值
     * @param timeout 时间 默认时间单位秒
     */
    public static <T> void setCacheObject(final String key, final T value, final Integer timeout) {
        setCacheObject(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public static <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return getRedisTemplate().expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = getRedisTemplate().opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public static boolean deleteObject(final String key) {
        return getRedisTemplate().delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public static long deleteObject(final Collection collection) {
        return getRedisTemplate().delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = getRedisTemplate().opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }


    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @param timeout  缓存时长 默认单位为秒
     * @return 缓存的对象
     */
    public static <T> long setCacheList(final String key, final List<T> dataList, final long timeout) {
        return setCacheList(key, dataList, timeout, TimeUnit.SECONDS);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     * @return 缓存的对象
     */
    public static <T> long setCacheList(final String key, final List<T> dataList, final long timeout, final TimeUnit timeUnit) {
        Long count = setCacheList(key, dataList);
        expire(key, timeout, timeUnit);
        return count == null ? 0 : count;
    }


    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getCacheList(final String key) {
        return getRedisTemplate().opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = getRedisTemplate().boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @param timeout 缓存时长 默认单位秒
     * @return 缓存数据的对象
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet, final long timeout) {
        return setCacheSet(key, dataSet, timeout, TimeUnit.SECONDS);
    }

    /**
     * 缓存Set
     *
     * @param key      缓存键值
     * @param dataSet  缓存的数据
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     * @return 缓存数据的对象
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet, final long timeout, final TimeUnit timeUnit) {
        BoundSetOperations<String, T> setOperation = setCacheSet(key, dataSet);
        expire(key, timeout, timeUnit);
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public static <T> Set<T> getCacheSet(final String key) {
        return getRedisTemplate().opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            getHashOperations().putAll(key, dataMap);
        }
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap, final long timeout, final TimeUnit timeUnit) {
        if (dataMap != null) {
            getHashOperations().putAll(key, dataMap);
            expire(key,timeout,timeUnit);
        }
    }

    /**
     * 增加hash的value值
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static Long incrementMapValue(final String key, final String hKey, final Long value) {
        return getHashOperations().increment(key, hKey, value);
    }


    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static <T> void setMapValue(final String key, final String hKey, final T value) {
        getHashOperations().put(key, hKey, value);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key     Redis键
     * @param hKey    Hash键
     * @param value   值
     * @param timeout 缓存时长 默认单位为秒
     */
    public static <T> void setMapValue(final String key, final String hKey, final T value, final long timeout) {
        setMapValue(key, hKey, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key      Redis键
     * @param hKey     Hash键
     * @param value    值
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    public static <T> void setMapValue(final String key, final String hKey, final T value, final long timeout, final TimeUnit timeUnit) {
        setMapValue(key, hKey, value);
        expire(key, timeout, timeUnit);
    }


    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public static <T> Map<String, T> getCacheMap(final String key) {
        return getHashOperations().entries(key);
    }


    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = getHashOperations();
        return opsForHash.get(key, hKey);
    }

    public static Long getCacheMapLong(final String key, final String hKey) {
        Integer value = getCacheMapValue(key,hKey);
        return value == null ? null : value.longValue();
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public static void delCacheMapValue(final String key, final String hkey) {
        getHashOperations().delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public static <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return getHashOperations().multiGet(key, hKeys);
    }

    private static HashOperations getHashOperations() {
        return getRedisTemplate().opsForHash();
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(final String pattern) {
        return getRedisTemplate().keys(pattern);
    }

}
