package com.jfnice.kit;

import com.jfinal.kit.StrKit;

/**
 * 字符串辅助类-暂不用到
 */
public class StrUtil {

    public static String termStr(String str) {
        if (StrKit.isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (String cls : str.split(",")) {
            if (StrKit.notBlank(cls)) {
                sb.append(",").append(cls.trim());
            }
        }
        str = sb.length() > 0 ? sb.substring(1) : "";
        return str;
    }

}
