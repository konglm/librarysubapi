package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMcType;

import java.util.*;

public class SysMcTypeMap {

    public static final SysMcTypeMap me = new SysMcTypeMap();
    private Object key = getClass().getSimpleName();

    public List<SysMcType> getList() {
        List<SysMcType> list = JsyApi.getSysMcTypeList(-1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMcType> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_MCTYPE, key) == null) {
            List<SysMcType> list = getList();
            if (list != null) {
                Map<String, SysMcType> map = new LinkedHashMap<>();
                for (SysMcType obj : list) {
                    map.put(obj.getMchtpcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_MCTYPE, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_MCTYPE, key), new TypeReference<LinkedHashMap<String, SysMcType>>() {
        });
    }

    public SysMcType get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getMchtpname();
        }
        return null;
    }

    public List<SysMcType> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_MCTYPE, key);
    }

}
