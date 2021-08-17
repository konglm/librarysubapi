package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysSub;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysSubMap {

    public static final SysSubMap me = new SysSubMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysSub> getList() {
        List<SysSub> list = JsyApi.getSysSubList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysSub> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysSub> map = JsyCacheKit.get(CacheName.JSY_SYS_SUB, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysSub obj : getList()) {
                map.put(obj.getSubcode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_SUB, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_SUB);
    }

}
