package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.CacheName;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysGrade;

import java.util.*;

public class SysGradeMap {

    public static final SysGradeMap me = new SysGradeMap();
    private Object key = getClass().getSimpleName();

    public List<SysGrade> getList() {
        return JsyApi.getSysGradeList();
    }

    public LinkedHashMap<String, SysGrade> getMap() {
        if (J2CacheShareKit.get(CacheName.JSY_SYS_GRADE, key) == null) {
            List<SysGrade> list = getList();
            if (list != null) {
                Map<String, SysGrade> map = new LinkedHashMap<>();
                for (SysGrade obj : list) {
                    map.put(obj.getSysgrdcode(), obj);
                }
                J2CacheShareKit.put(CacheName.JSY_SYS_GRADE, key, JsonKit.toJson(map));
            }
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.JSY_SYS_GRADE, key), new TypeReference<LinkedHashMap<String, SysGrade>>() {
        });
    }

    public SysGrade get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSysgrdname();
        }
        return null;
    }


    public List<SysGrade> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        J2CacheShareKit.remove(CacheName.JSY_SYS_GRADE, key);
    }
}
