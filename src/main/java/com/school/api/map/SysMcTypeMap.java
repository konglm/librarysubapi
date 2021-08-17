package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysMcType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysMcTypeMap {

    public static final SysMcTypeMap me = new SysMcTypeMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysMcType> getList() {
        List<SysMcType> list = JsyApi.getSysMcTypeList(-1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysMcType> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysMcType> map = JsyCacheKit.get(CacheName.JSY_SYS_MCTYPE, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysMcType obj : getList()) {
                map.put(obj.getMchtpcode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_MCTYPE, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_MCTYPE);
    }

}
