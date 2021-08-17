package com.jfnice.cache;

/**
 * 缓存辅助类
 *
 * @author jsy
 * @date 2021-07-27
 */
public class JsyCacheKit {

    public static final JsyCache MY_CACHE = new JsyCache();

    public static <T> T get(String cacheName, Object key) {
        return MY_CACHE.get(cacheName, key);
    }

    /**
     * 加入缓存
     *
     * @param cacheName 缓存名
     * @param key       键
     * @param value     值
     */
    public static void put(String cacheName, Object key, Object value) {
        MY_CACHE.put(cacheName, key.toString(), value);
    }

    /**
     * 加入缓存且有有效时间
     *
     * @param cacheName           缓存名
     * @param key                 键
     * @param value               值
     * @param timeToLiveInSeconds 有效时长 单位秒
     */
    public static void put(String cacheName, Object key, Object value, long timeToLiveInSeconds) {
        MY_CACHE.put(cacheName, key.toString(), value, timeToLiveInSeconds);
    }

    /**
     * 移除key
     *
     * @param cacheName 缓存名
     * @param key       键
     */
    public static void remove(String cacheName, Object key) {
        MY_CACHE.remove(cacheName, key.toString());
    }

    /**
     * 移除某个空间所有缓存
     *
     * @param cacheName 缓存名
     */
    public static void removeAll(String cacheName) {
        MY_CACHE.removeAll(cacheName);
    }

}
