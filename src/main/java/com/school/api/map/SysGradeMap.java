package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysGrade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysGradeMap {

    public static final SysGradeMap me = new SysGradeMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysGrade> getList() {
        return JsyApi.getSysGradeList();
    }

    public LinkedHashMap<String, SysGrade> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysGrade> map = JsyCacheKit.get(CacheName.JSY_SYS_GRADE, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysGrade obj : getList()) {
                map.put(obj.getSysgrdcode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_GRADE, key, map, time);
        }
        return map;
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
        JsyCacheKit.removeAll(CacheName.JSY_SYS_GRADE);
    }
}
