package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMater;

import java.util.*;

public class SysMaterMap {

    public static final SysMaterMap me = new SysMaterMap();
    private Object key = getClass().getSimpleName();

    public List<SysMater> getList() {
        List<SysMater> list = JsyApi.getSysMaterList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMater> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_MATER, key) == null) {
            List<SysMater> list = getList();
            if (list != null) {
                Map<String, SysMater> map = new LinkedHashMap<>();
                for (SysMater obj : list) {
                    map.put(obj.getMatercode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_MATER, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_MATER, key), new TypeReference<LinkedHashMap<String, SysMater>>() {
        });
    }

    public SysMater get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getMatername();
        }
        return null;
    }


    public List<SysMater> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_MATER, key);
    }

}
