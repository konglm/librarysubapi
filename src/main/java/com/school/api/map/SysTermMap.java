package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysTerm;

import java.util.*;

public class SysTermMap {

    public static final SysTermMap me = new SysTermMap();
    private Object key = getClass().getSimpleName();

    public List<SysTerm> getList() {
        List<SysTerm> list = JsyApi.getSysTermList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysTerm> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_TERM, key) == null) {
            List<SysTerm> list = getList();
            if (list != null) {
                Map<String, SysTerm> map = new LinkedHashMap<>();
                for (SysTerm obj : list) {
                    map.put(obj.getTermcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_TERM, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_TERM, key), new TypeReference<LinkedHashMap<String, SysTerm>>() {
        });
    }

    public SysTerm get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getTermname();
        }
        return null;
    }

    public List<SysTerm> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_TERM, key);
    }

}
