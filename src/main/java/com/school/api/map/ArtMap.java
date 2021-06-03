package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Art;

import java.util.*;

/**
 * 学校分科缓存
 */
public class ArtMap {

    public static final ArtMap me = new ArtMap();

    private List<Art> getList() {
        return Optional.ofNullable(RsApi.getArt()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Art> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_ART_MAP, schCode) == null) {
            List<Art> list = getList();
            Map<String, Art> map = new LinkedHashMap<>();
            for (Art obj : list) {
                map.put(obj.getArtCode(), obj);
            }
            J2CacheShareKit.put(CacheName.SCH_ART_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_ART_MAP, schCode), new TypeReference<LinkedHashMap<String, Art>>() {
        });
    }

    public Art get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getArtName();
        }
        return null;
    }

    public List<Art> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.SCH_ART_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_ART_MAP);
    }

}
