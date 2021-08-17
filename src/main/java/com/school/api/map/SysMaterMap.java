package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMater;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysMaterMap {

    public static final SysMaterMap me = new SysMaterMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysMater> getList() {
        List<SysMater> list = JsyApi.getSysMaterList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMater> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysMater> map = JsyCacheKit.get(CacheName.JSY_SYS_MATER, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysMater obj : getList()) {
                map.put(obj.getMatercode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_MATER, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_MATER);
    }

}
