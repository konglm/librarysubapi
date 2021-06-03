package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Year;

import java.util.*;

/**
 * 学年缓存
 */
public class YearMap {

    public static final YearMap me = new YearMap();
    private Object key = getClass().getSimpleName();

    private List<Year> getList() {
        List<Year> list = RsApi.getYearList();
        return Optional.ofNullable(list).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Year> getMap() {
        if (J2CacheShareKit.get(CacheName.SCH_YEAR_MAP, key) == null) {
            List<Year> list = getList();
            Map<String, Year> map = new LinkedHashMap<>();
            for (Year obj : list) {
                map.put(obj.getYearCode(), obj);
            }
            J2CacheShareKit.put(CacheName.SCH_YEAR_MAP, key, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_YEAR_MAP, key), new TypeReference<LinkedHashMap<String, Year>>() {
        });
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
        J2CacheShareKit.remove(CacheName.SCH_YEAR_MAP, key);
    }
}
