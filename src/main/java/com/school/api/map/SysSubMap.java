package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysSub;

import java.util.*;

public class SysSubMap {

    public static final SysSubMap me = new SysSubMap();
    private Object key = getClass().getSimpleName();

    public List<SysSub> getList() {
        List<SysSub> list = JsyApi.getSysSubList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysSub> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_SUB, key) == null) {
            List<SysSub> list = getList();
            if (list != null) {
                Map<String, SysSub> map = new LinkedHashMap<>();
                for (SysSub obj : list) {
                    map.put(obj.getSubcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_SUB, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_SUB, key), new TypeReference<LinkedHashMap<String, SysSub>>() {
        });
    }

    public SysSub get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSubname();
        }
        return null;
    }


    public List<SysSub> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_SUB, key);
    }

}
