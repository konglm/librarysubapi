package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysPer;

import java.util.*;

public class SysPerMap {

    public static final SysPerMap me = new SysPerMap();
    private Object key = getClass().getSimpleName();

    public List<SysPer> getList() {
        List<SysPer> list = JsyApi.getSysPerList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysPer> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_PER, key) == null) {
            List<SysPer> list = getList();
            if (list != null) {
                Map<String, SysPer> map = new LinkedHashMap<>();
                for (SysPer obj : list) {
                    map.put(obj.getPercode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_PER, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_PER, key), new TypeReference<LinkedHashMap<String, SysPer>>() {
        });
    }

    public SysPer get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getPername();
        }
        return null;
    }

    public String year(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getPeryear();
        }
        return null;
    }

    public List<SysPer> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_PER, key);
    }

}
