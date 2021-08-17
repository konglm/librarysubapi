package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Sub;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学校科目缓存
 */
public class SubMap {

    public static final SubMap me = new SubMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Sub> getList() {
        return Optional.ofNullable(RsApi.getSubList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Sub> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Sub> map = JsyCacheKit.get(CacheName.SCH_SUB_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Sub obj : getList()) {
                map.put(obj.getSubCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_SUB_MAP, key, map, time);
        }
        return map;
    }

    public Sub get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSubName();
        }
        return null;
    }

    public List<Sub> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_SUB_MAP);
    }

}
