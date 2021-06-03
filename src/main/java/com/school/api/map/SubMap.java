package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Sub;

import java.util.*;

/**
 * 学校科目缓存
 */
public class SubMap {

    public static final SubMap me = new SubMap();

    private List<Sub> getList() {
        return Optional.ofNullable(RsApi.getSubList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Sub> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_SUB_MAP, schCode) == null) {
            Map<String, Sub> map = new LinkedHashMap<>();
            for (Sub sub : getList()) {
                map.put(sub.getSubCode(), sub);
            }
            J2CacheShareKit.put(CacheName.SCH_SUB_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_SUB_MAP, schCode), new TypeReference<LinkedHashMap<String, Sub>>() {
        });
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
        J2CacheShareKit.remove(CacheName.SCH_SUB_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_SUB_MAP);
    }

}
