package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.enums.FinishEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Grd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 学校年级缓存
 */
public class GrdMap {

    public static final GrdMap me = new GrdMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Grd> getList() {
        return Optional.ofNullable(RsApi.getGrdList(FinishEnum.ALL.getK())).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Grd> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Grd> map = JsyCacheKit.get(CacheName.SCH_GRD_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Grd obj : getList()) {
                map.put(obj.getGrdCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_GRD_MAP, key, map, time);
        }
        return map;
    }

    public Grd get(String code) {
        return getMap().get(code);
    }

    public String sysCode(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSysGrdCode();
        }
        return null;
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getGrdName();
        }
        return null;
    }

    public List<Grd> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_GRD_MAP);
    }

}
