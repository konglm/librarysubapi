package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysFasc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysFascMap {

    public static final SysFascMap me = new SysFascMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysFasc> getList() {
        List<SysFasc> list = JsyApi.getSysFascList();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysFasc> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysFasc> map = JsyCacheKit.get(CacheName.JSY_SYS_FASC, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysFasc obj : getList()) {
                map.put(obj.getFasccode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_FASC, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_FASC);
    }

}
