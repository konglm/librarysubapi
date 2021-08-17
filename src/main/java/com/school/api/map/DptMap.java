package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Dpt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学校部门缓存
 */
public class DptMap {

    public static final DptMap me = new DptMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Dpt> getList() {
        return Optional.ofNullable(RsApi.getDptList()).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Dpt> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Dpt> map = JsyCacheKit.get(CacheName.SCH_DPT_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Dpt obj : getList()) {
                map.put(obj.getDptCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_DPT_MAP, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.SCH_DPT_MAP);
    }
}
