/**
 * Copyright (c) 2016-2017, JFnice (www.jfnice.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfnice.j2cache;

import net.oschina.j2cache.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 缓存辅助类
 */
public class J2CacheKit {

    private final static String CONFIG_FILE = "/j2cache.properties";
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
     * 获取缓存空间里key的值
     * @param cacheName 缓存空间名
     * @param key 键
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
     * 加入缓存且有有效时间
     * @param cacheName
     * @param key
     * @param value
     * @param timeToLiveInSeconds
     */
    public static void put(String cacheName, Object key, Object value, long timeToLiveInSeconds) {
        cacheChannel.set(cacheName, key.toString(), value, timeToLiveInSeconds);
    }

    /**
     * 移除key
     * @param cacheName
     * @param key
     */
    public static void remove(String cacheName, Object key) {
        cacheChannel.evict(cacheName, key.toString());
    }

    /**
     * 移除某个空间所有缓存
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
