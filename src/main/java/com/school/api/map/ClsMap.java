package com.school.api.map;

import com.jfinal.kit.StrKit;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.FinishEnum;
import com.jfnice.ext.CurrentUser;
import com.school.api.gx.RsApi;
import com.school.api.model.Cls;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学校班级缓存
 */
public class ClsMap {

    public static final ClsMap me = new ClsMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    private List<Cls> getList() {
        return Optional.ofNullable(RsApi.getClsList(FinishEnum.ALL.getK())).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Cls> getMap() {
        String key = CurrentUser.getSchoolCode() + ":" + CurrentUser.getAccessToken();
        LinkedHashMap<String, Cls> map = JsyCacheKit.get(CacheName.SCH_CLS_MAP, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (Cls obj : getList()) {
                map.put(obj.getClsCode(), obj);
            }
            JsyCacheKit.put(CacheName.SCH_CLS_MAP, key, map, time);
        }
        return map;
    }

    public Cls get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getClsName();
        }
        return null;
    }

    public List<Cls> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    /**
     * 获取年级下班级
     *
     * @param grdCodes 年级ids
     * @return 班级集合
     */
    public List<Cls> getListByGrdIds(String grdCodes) {
        // 获取所有的年级id，注意去除空格
        List<String> gidList = StrKit.isBlank(grdCodes) ? new ArrayList<>() : Arrays.stream(grdCodes.split(",")).map(String::trim).collect(Collectors.toList());
        if (getMap() == null) {
            return null;
        }
        return getMap().values().stream().filter(obj -> StrKit.isBlank(grdCodes) || gidList.contains(obj.getGrdCode())).collect(Collectors.toList());
    }

    public String list2Ids(List<Cls> clsList) {
        StringBuilder clsIds = new StringBuilder();
        Optional.ofNullable(clsList).ifPresent(list -> list.forEach(cls -> clsIds.append(",").append(cls.getGrdCode())));
        return clsIds.length() > 1 ? clsIds.substring(1) : "";
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.SCH_CLS_MAP);
    }

}
