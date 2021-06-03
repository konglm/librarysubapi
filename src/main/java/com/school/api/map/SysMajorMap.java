package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMajor;

import java.util.*;

public class SysMajorMap {

    public static final SysMajorMap me = new SysMajorMap();
    private Object key = getClass().getSimpleName();

    public List<SysMajor> getList() {
        List<SysMajor> list = JsyApi.getSysMajorList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMajor> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_MAJOR, key) == null) {
            List<SysMajor> list = getList();
            if (list != null) {
                Map<String, SysMajor> map = new LinkedHashMap<>();
                for (SysMajor obj : list) {
                    map.put(obj.getMajorcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_MAJOR, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_MAJOR, key), new TypeReference<LinkedHashMap<String, SysMajor>>() {
        });
    }

    public SysMajor get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getMajorname();
        }
        return null;
    }


    public List<SysMajor> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_MAJOR, key);
    }

}
