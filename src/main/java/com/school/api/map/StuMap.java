package com.school.api.map;

import com.jfnice.cache.JsyCacheKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.school.api.gx.RsApi;
import com.school.api.model.Cls;
import com.school.api.model.Stu;

import java.util.*;

/**
 * 学校学生缓存
 */
public class StuMap {

    public static final StuMap me = new StuMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Stu> getList() {
        List<Stu> stuList = new ArrayList<>();
        StringBuilder clsCodes = new StringBuilder();
        if (ClsMap.me.getMap() != null) {
            for (Map.Entry<String, Cls> entry : ClsMap.me.getMap().entrySet()) {
                clsCodes.append(",").append(entry.getKey());
            }
        }
        if (clsCodes.length() > 1) {
            stuList = RsApi.getStuList(clsCodes.substring(1));
        }
        return stuList;
    }

    public Map<String, Stu> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Stu> map = JsyCacheKit.get(CacheName.SCH_STU_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Stu obj : getList()) {
                map.put(obj.getStuCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_STU_MAP, key, map, time);
        }
        return map;
    }

    public Stu get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getStuName();
        }
        return null;
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_STU_MAP);
    }

}
