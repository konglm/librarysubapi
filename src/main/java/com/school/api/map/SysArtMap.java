package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysArt;

import java.util.*;

public class SysArtMap {

    public static final SysArtMap me = new SysArtMap();
    private Object key = getClass().getSimpleName();

    public List<SysArt> getList() {
        List<SysArt> list = JsyApi.getSysArtList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysArt> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_ART, key) == null) {
            List<SysArt> list = getList();
            if (list != null) {
                Map<String, SysArt> map = new LinkedHashMap<>();
                for (SysArt art : list) {
                    map.put(art.getArtscode(), art);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_ART, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_ART, key), new TypeReference<LinkedHashMap<String, SysArt>>() {
        });
    }

    public SysArt get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getArtsname();
        }
        return null;
    }

    public List<SysArt> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_ART, key);
    }

}
