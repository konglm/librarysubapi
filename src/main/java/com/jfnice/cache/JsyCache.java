package com.jfnice.cache;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
 * JsyCache
 *
 * @author jsy
 * @date 2021/7/26
 */
public class JsyCache implements ICache {

    private static String getCacheType() {
        return PropKit.get("cache.type", "ehcache");
    }

    public static boolean isEhCache() {
        return "ehcache".equals(getCacheType());
    }

    public static boolean isRedis() {
        return "redis".equals(getCacheType());
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        if (isEhCache()) {
            return CacheKit.get(cacheName, key);
        } else if (isRedis()) {
            return Redis.use().get(cacheName + ":" + key);
        }
        return null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        if (isEhCache()) {
            CacheKit.put(cacheName, key, value);
        } else if (isRedis()) {
            Redis.use().set(cacheName + ":" + key, value);
        }
    }

    public void put(String cacheName, Object key, Object value, long time) {
        if (isEhCache()) {
            CacheKit.put(cacheName, key, value);
        } else if (isRedis()) {
            Redis.use().setex(cacheName + ":" + key, (int) time, value);
        }
    }

    @Override
    public void remove(String cacheName, Object key) {
        if (isEhCache()) {
            CacheKit.remove(cacheName, key);
        } else if (isRedis()) {
            Redis.use().del(cacheName + ":" + key);
        }
    }

    @Override
    public void removeAll(String cacheName) {
        if (isEhCache()) {
            CacheKit.removeAll(cacheName);
        } else if (isRedis()) {
            Cache cache = Redis.use();
            Object[] keys = cache.keys(cacheName + ":*").toArray();
            if (keys.length > 0) {
                cache.del(keys);
            }
        }
    }

}
