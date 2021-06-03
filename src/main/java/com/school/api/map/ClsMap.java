package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.FinishEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Cls;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学校班级缓存
 */
public class ClsMap {

    public static final ClsMap me = new ClsMap();

    private List<Cls> getList() {
        return Optional.ofNullable(RsApi.getClsList(FinishEnum.ALL.getK())).orElse(new ArrayList<>());
    }

    public LinkedHashMap<String, Cls> getMap() {
        String schCode = CurrentUser.getSchoolCode();
        if (J2CacheShareKit.get(CacheName.SCH_CLS_MAP, schCode) == null) {
            Map<String, Cls> map = new LinkedHashMap<>();
            for (Cls cls : getList()) {
                map.put(cls.getClsCode(), cls);
            }
            J2CacheShareKit.put(CacheName.SCH_CLS_MAP, schCode, JsonKit.toJson(map));
        }
        return JSON.parseObject(J2CacheShareKit.get(CacheName.SCH_CLS_MAP, schCode), new TypeReference<LinkedHashMap<String, Cls>>() {
        });
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
        J2CacheShareKit.remove(CacheName.SCH_CLS_MAP, CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_CLS_MAP);
    }

}
