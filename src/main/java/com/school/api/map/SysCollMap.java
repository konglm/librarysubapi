package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysColl;

import java.util.*;

public class SysCollMap {

    public static final SysCollMap me = new SysCollMap();
    private Object key = getClass().getSimpleName();

    public List<SysColl> getList() {
        List<SysColl> list = JsyApi.getSysCollList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysColl> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_COLL, key) == null) {
            List<SysColl> list = getList();
            if (list != null) {
                Map<String, SysColl> map = new LinkedHashMap<>();
                for (SysColl obj : list) {
                    map.put(obj.getCollcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_COLL, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_COLL, key), new TypeReference<LinkedHashMap<String, SysColl>>() {
        });
    }

    public SysColl get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getCollname();
        }
        return null;
    }


    public List<SysColl> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_COLL, key);
    }

}
