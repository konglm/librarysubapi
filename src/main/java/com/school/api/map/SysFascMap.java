package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysFasc;

import java.util.*;

public class SysFascMap {

    public static final SysFascMap me = new SysFascMap();
    private Object key = getClass().getSimpleName();

    public List<SysFasc> getList() {
        List<SysFasc> list = JsyApi.getSysFascList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysFasc> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_FASC, key) == null) {
            List<SysFasc> list = getList();
            if (list != null) {
                Map<String, SysFasc> map = new LinkedHashMap<>();
                for (SysFasc obj : list) {
                    map.put(obj.getFasccode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_FASC, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_FASC, key), new TypeReference<LinkedHashMap<String, SysFasc>>() {
        });
    }

    public SysFasc get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getFascname();
        }
        return null;
    }


    public List<SysFasc> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_FASC, key);
    }

}
