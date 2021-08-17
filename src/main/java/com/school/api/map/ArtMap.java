package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Art;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学校分科缓存
 */
public class ArtMap {

    public static final ArtMap me = new ArtMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Art> getList() {
        return Optional.ofNullable(RsApi.getArt()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Art> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Art> map = JsyCacheKit.get(CacheName.SCH_ART_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Art obj : getList()) {
                map.put(obj.getArtCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_ART_MAP, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.SCH_ART_MAP);
    }

}
