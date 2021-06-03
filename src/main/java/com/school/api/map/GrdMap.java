package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.FinishEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Grd;

import java.util.*;

/**
 * 学校年级缓存
 */
public class GrdMap {

    public static final GrdMap me = new GrdMap();

    private List<Grd> getList() {
        return Optional.ofNullable(RsApi.getGrdList(FinishEnum.ALL.getK())).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Grd> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_GRD_MAP, schCode) == null) {
            Map<String, Grd> map = new LinkedHashMap<>();
            for (Grd grd : getList()) {
                map.put(grd.getGrdCode(), grd);
            }
            J2CacheShareKit.put(CacheName.SCH_GRD_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_GRD_MAP, schCode), new TypeReference<LinkedHashMap<String, Grd>>() {
        });
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
        J2CacheShareKit.remove(CacheName.SCH_GRD_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_GRD_MAP);
    }

}
