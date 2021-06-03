package com.jfnice.j2cache;

import net.oschina.j2cache.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * shiro session缓存辅助类-已作废
 */
public class J2CacheShiroSessionKit {

    private final static String CONFIG_FILE = "/j2cache-shiro-session.properties";
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

    public static void put(String cacheName, Object key, Object value) {
        cacheChannel.set(cacheName, key.toString(), value);
    }

    public static void put(String cacheName, Object key, Object value, long timeToLiveInSeconds) {
        cacheChannel.set(cacheName, key.toString(), value, timeToLiveInSeconds);
    }

    public static void remove(String cacheName, Object key) {
        cacheChannel.evict(cacheName, key.toString());
    }

    public static void removeAll(String cacheName) {
        cacheChannel.clear(cacheName);
    }

    public static Collection<String> keys(String cacheName) {
        return cacheChannel.keys(cacheName);
    }

    public static Collection<Object> values(String cacheName) {
        Map<String, CacheObject> map = cacheChannel.get(cacheName, cacheChannel.keys(cacheName));
        List<Object> values = new ArrayList<Object>();
        for (Map.Entry<String, CacheObject> entry : map.entrySet()) {
            values.add(entry.getValue().getValue());
        }
        return values;
    }

    public static int size(String cacheName) {
        return cacheChannel.keys(cacheName).size();
    }

}
