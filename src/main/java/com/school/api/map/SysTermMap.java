package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysTerm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysTermMap {

    public static final SysTermMap me = new SysTermMap();
    public long time = 2 * 60 * 60;

    public List<SysTerm> getList() {
        List<SysTerm> list = JsyApi.getSysTermList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysTerm> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysTerm> map = JsyCacheKit.get(CacheName.JSY_SYS_TERM, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysTerm obj : getList()) {
                map.put(obj.getTermcode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_TERM, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_TERM);
    }

}
