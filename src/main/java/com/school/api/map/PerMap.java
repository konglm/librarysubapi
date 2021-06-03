package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Per;

import java.util.*;

/**
 * 学校学段缓存
 */
public class PerMap {

    public static final PerMap me = new PerMap();

    private List<Per> getList() {
        return Optional.ofNullable(RsApi.getPerList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Per> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_PER_MAP, schCode) == null) {
            Map<String, Per> map = new LinkedHashMap<>();
            for (Per per : getList()) {
                map.put(per.getPerCode(), per);
            }
            J2CacheShareKit.put(CacheName.SCH_PER_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_PER_MAP, schCode), new TypeReference<LinkedHashMap<String, Per>>() {
        });
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
        J2CacheShareKit.remove(CacheName.SCH_PER_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_PER_MAP);
    }

}
