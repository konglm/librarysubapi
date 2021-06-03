package com.school.api.map;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheShareKit;
import com.school.api.gx.RsApi;
import com.school.api.model.Cls;
import com.school.api.model.Stu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学校学生缓存
 */
public class StuMap {

    public static final StuMap me = new StuMap();

    private List<Stu> getList(String clsCodes) {
        List<Stu> stuList = new ArrayList<>();
        if (StrKit.notBlank(clsCodes)) {
            stuList = RsApi.getStuList(clsCodes);
        }
        return stuList;
    }

    /**
     * 缓存中获取数据
     *
     * @param schCode 学校id
     * @param clsCode 班级id
     * @return List<Stu>
     */
    private List<Stu> getListByClsIdFromCache(String schCode, String clsCode) {
        if (J2CacheShareKit.get(CacheName.SCH_STU_MAP + ":" + schCode, clsCode) == null) {
            return null;
        }
        return JSON.parseArray(J2CacheShareKit.get(CacheName.SCH_STU_MAP + ":" + schCode, clsCode), Stu.class);
    }

    /**
     * 获取班级下的学生
     *
     * @param clsCodes 班级ids
     * @return 学生列表
     */
    public List<Stu> getListByClsIds(String clsCodes) {
        String schCode = CurrentUser.getSchoolCode();
        // 获取所有的年级id，注意去除空格
        List<String> clsCodeList = StrKit.isBlank(clsCodes) ? new ArrayList<>() : Arrays.stream(clsCodes.split(",")).map(String::trim).collect(Collectors.toList());
        List<Stu> stuList = new ArrayList<>(); // 学生列表
        StringBuilder noCacheClsCodes = new StringBuilder();
        clsCodeList.forEach(cCode -> {
            List<Stu> clsStuList = getListByClsIdFromCache(schCode, cCode);
            if (clsStuList == null) {
                noCacheClsCodes.append(",").append(cCode);
            } else {
                stuList.addAll(clsStuList);
            }
        });
        if (noCacheClsCodes.length() > 1) {
            List<Stu> noCacheList = getList(noCacheClsCodes.substring(1));
            Map<String, List<Stu>> clsStuMap = noCacheList.stream().collect(Collectors.groupingBy(Stu::getClsCode));

            for (String code : noCacheClsCodes.substring(1).split(",")) {
                List<Stu> clsStuList = clsStuMap.get(code);
                J2CacheShareKit.put(CacheName.SCH_STU_MAP + ":" + schCode, code, JsonKit.toJson(clsStuList == null ? new ArrayList<>() : clsStuList));
            }

            stuList.addAll(noCacheList);
        }

        return stuList;
    }

    /**
     * 获取年级下的学生
     *
     * @param grdCodes 年级ids
     * @return 学生列表
     */
    public List<Stu> getListByGrdCodes(String grdCodes) {
        return getListByClsIds(ClsMap.me.list2Ids(ClsMap.me.getListByGrdIds(grdCodes)));
    }

    /**
     * 获取全校学生
     *
     * @return List<Stu>
     */
    public List<Stu> getSchStuList() {
        StringBuilder clsCodes = new StringBuilder();
        if (ClsMap.me.getMap() != null) {
            for (Map.Entry<String, Cls> entry : ClsMap.me.getMap().entrySet()) {
                clsCodes.append(",").append(entry.getKey());
            }
        }
        if (clsCodes.length() > 1) {
            return getListByClsIds(clsCodes.substring(1));
        }
        return new ArrayList<>();
    }

    public Map<String, Stu> getMap() {
        return getSchStuList().stream().collect(Collectors.toMap(Stu::getStuCode, a -> a, (k1, k2) -> k1));
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

    public String sno(String schCode, String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getSno();
        }
        return null;
    }

    /**
     * 根据学号查询本学校的学生
     */
    public Stu getBySno(String sno) {
        return getMap().values().stream().filter(stu -> sno.equals(stu.getSno())).findFirst().orElse(new Stu());
    }

    public void clear() {
        J2CacheShareKit.removeAll(CacheName.SCH_STU_MAP + ":" + CurrentUser.getSchoolCode());
    }

    public void clearAll() {
        J2CacheShareKit.removeAll(CacheName.SCH_STU_MAP);
    }

}
