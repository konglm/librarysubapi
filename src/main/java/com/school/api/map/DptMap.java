package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Dpt;

import java.util.*;

/**
 * 学校部门缓存
 */
public class DptMap {

    public static final DptMap me = new DptMap();

    private List<Dpt> getList() {
        return Optional.ofNullable(RsApi.getDptList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Dpt> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_DPT_MAP, schCode) == null) {
            Map<String, Dpt> map = new LinkedHashMap<>();
            for (Dpt dpt : getList()) {
                map.put(dpt.getDptCode(), dpt);
            }
            J2CacheShareKit.put(CacheName.SCH_DPT_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_DPT_MAP, schCode), new TypeReference<LinkedHashMap<String, Dpt>>() {
        });
    }

    public Dpt get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getDptName();
        }
        return null;
    }

    public List<Dpt> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.SCH_DPT_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_DPT_MAP);
    }

}
