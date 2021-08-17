package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysArt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysArtMap {

    public static final SysArtMap me = new SysArtMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysArt> getList() {
        List<SysArt> list = JsyApi.getSysArtList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysArt> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysArt> map = JsyCacheKit.get(CacheName.JSY_SYS_ART, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysArt obj : getList()) {
                map.put(obj.getArtscode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_ART, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_ART);
    }

}
