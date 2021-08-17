package com.school.api.map;

import com.jfnice.commons.CacheName;
import com.jfnice.enums.AreaTypeEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.cache.JsyCacheKit;
import com.school.api.jsy.JsyApi;
import com.school.api.model.SysArea;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class SysAreaMap {

    public static final SysAreaMap me = new SysAreaMap();
    /**
     * 缓存时间
     */
    public long time = 2 * 60 * 60;

    public List<SysArea> getList() {
        List<SysArea> list = JsyApi.getSysAreaList(AreaTypeEnum.ALL_AREA.getK(), null);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public LinkedHashMap<String, SysArea> getMap() {
        String key = Optional.ofNullable(CurrentUser.getAccessToken()).orElse("");
        LinkedHashMap<String, SysArea> map = JsyCacheKit.get(CacheName.JSY_SYS_AREA, key);
        if (map == null) {
            map = new LinkedHashMap<>();
            for (SysArea obj : getList()) {
                map.put(obj.getAreaCode(), obj);
            }
            JsyCacheKit.put(CacheName.JSY_SYS_AREA, key, map, time);
        }
        return map;
    }

    public SysArea get(String code) {
        return getMap().get(code);
    }

    public String name(String code) {
        if (getMap().get(code) != null) {
            return getMap().get(code).getAreaName();
        }
        return null;
    }

    /**
     * 去除code最后面的0
     *
     * @param code code
     * @return 去除0之后的code
     */
    public String exZeroCode(String code) {
        if (StringUtils.isEmpty(code))
            return code;
        int initLen = code.length(); // 串的初始长度
        int finalLen = initLen; // 串的最终长度
        int start = 0; // 串的开始位置
        int off = 0; // 串的偏移位置
        char[] val = new char[initLen];
        code.getChars(0, finalLen, val, 0); // 保存原数据，用于判断字符
        // 找到以'0'结尾的前一位
        while ((start < finalLen) && (val[off + finalLen - 1] == '0')) {
            finalLen--;
        }
        return finalLen < initLen ? code.substring(start, finalLen) : code;
    }

    public List<SysArea> toArray() {
        return new ArrayList<>(Optional.ofNullable(getMap()).orElse(new LinkedHashMap<>()).values());
    }

    public void clear() {
        JsyCacheKit.removeAll(CacheName.JSY_SYS_AREA);
    }

}
