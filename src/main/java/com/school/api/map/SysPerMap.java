package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysPer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysPerMap {

    public static final SysPerMap me = new SysPerMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysPer> getList() {
        List<SysPer> list = JsyApi.getSysPerList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysPer> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysPer> map = JsyCacheKit.get(CacheName.JSY_SYS_PER, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysPer obj : getList()) {
                map.put(obj.getPercode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_PER, key, map, time);
        }
        return map;
    }

    public SysPer get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getPername();
        }
        return null;
    }

    public String year(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getPeryear();
        }
        return null;
    }

    public List<SysPer> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.JSY_SYS_PER);
    }

}
