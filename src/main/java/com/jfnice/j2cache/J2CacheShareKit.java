package com.jfnice.j2cache;

import net.oschina.j2cache.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 共享缓存辅助类
 */
public class J2CacheShareKit {

    private final static String CONFIG_FILE = "/j2cache-share.properties";
    private final static J2CacheBuilder builder;
    private static CacheChannel cacheChannel;

    static {
        try {
            J2CacheConfig config = J2CacheConfig.initFromConfig(CONFIG_FILE);
            builder = J2CacheBuilder.init(config);
        } catch (IOException e) {
            throw new CacheException("Failed to load j2cache configuration " + CONFIG_FILE, e);
        }
    }

    /**
     * 初始化
     */
    public static void init() {
        if (cacheChannel == null) {
            System.setProperty("java.net.preferIPv4Stack", "true");
            cacheChannel = builder.getChannel();
        }
    }

    public static void destroy() {
        if (cacheChannel != null) {
            builder.close();
        }
    }

    /**
     * 获取缓存空间key的值
     * @param cacheName
     * @param key
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String cacheName, Object key) {
        if (key == null) {
            return null;
        }
        CacheObject cacheObject = cacheChannel.get(cacheName, key.toString());
        if (cacheObject == null) {
            return null;
        }
        return (T) cacheObject.getValue();
    }

    /**
     * 加入缓存
     * @param cacheName
     * @param key
     * @param value
     */
    public static void put(String cacheName, Object key, Object value) {
        cacheChannel.set(cacheName, key.toString(), value);
    }

    /**
     * 加入缓存且有有效期
     * @param cacheName
     * @param key
     * @param value
     * @param timeToLiveInSeconds
     */
    public static void put(String cacheName, Object key, Object value, long timeToLiveInSeconds) {
        cacheChannel.set(cacheName, key.toString(), value, timeToLiveInSeconds);
    }

    /**
     * 移除
     * @param cacheName
     * @param key
     */
    public static void remove(String cacheName, Object key) {
        cacheChannel.evict(cacheName, key.toString());
    }

    /**
     * 移除缓存空间所有key
     * @param cacheName
     */
    public static void removeAll(String cacheName) {
        cacheChannel.clear(cacheName);
    }

    /**
     * 获取缓存空间所有key
     * @param cacheName
     * @return
     */
    public static Collection<String> keys(String cacheName) {
        return cacheChannel.keys(cacheName);
    }

    /**
     * 获取缓存空间所有值
     * @param cacheName
     * @return
     */
    public static Collection<Object> values(String cacheName) {
        Map<String, CacheObject> map = cacheChannel.get(cacheName, cacheChannel.keys(cacheName));
        List<Object> values = new ArrayList<Object>();
        for (Map.Entry<String, CacheObject> entry : map.entrySet()) {
            values.add(entry.getValue().getValue());
        }
        return values;
    }

    /**
     * 获取缓存空间大小
     * @param cacheName
     * @return
     */
    public static int size(String cacheName) {
        return cacheChannel.keys(cacheName).size();
    }

}
