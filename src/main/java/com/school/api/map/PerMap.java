package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Per;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学校学段缓存
 */
public class PerMap {

    public static final PerMap me = new PerMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Per> getList() {
        return Optional.ofNullable(RsApi.getPerList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Per> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Per> map = JsyCacheKit.get(CacheName.SCH_PER_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Per obj : getList()) {
                map.put(obj.getPerCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_PER_MAP, key, map, time);
        }
        return map;
    }

    public Per get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        return SysPerMap.me.name(code);
    }

    public List<Per> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_PER_MAP);
    }
}
