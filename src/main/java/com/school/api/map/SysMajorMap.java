package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMajor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysMajorMap {

    public static final SysMajorMap me = new SysMajorMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysMajor> getList() {
        List<SysMajor> list = JsyApi.getSysMajorList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMajor> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysMajor> map = JsyCacheKit.get(CacheName.JSY_SYS_MAJOR, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysMajor obj : getList()) {
                map.put(obj.getMajorcode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_MAJOR, key, map, time);
        }
        return map;
    }

    public SysMajor get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getMajorname();
        }
        return null;
    }

    public List<SysMajor> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.JSY_SYS_MAJOR);
    }

}
