package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Year;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学年缓存
 */
public class YearMap {

    public static final YearMap me = new YearMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Year> getList() {
        List<Year> list = RsApi.getYearList();
        return Optional.ofNullable(list).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Year> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, Year> map = JsyCacheKit.get(CacheName.SCH_YEAR_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Year obj : getList()) {
                map.put(obj.getYearCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_YEAR_MAP, key, map, time);
        }
        return map;
    }

    public Year get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getYearName();
        }
        return null;
    }

    public List<Year> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_YEAR_MAP);
    }
}
