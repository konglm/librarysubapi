package com.jfnice.utils;

import com.jfinal.kit.PropKit;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignUtilTest {

    @Test
    public void getSign() {
        PropKit.useFirstFound("config.properties");
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("access_token", "ZGM2NTQyM2QtNTU0My00ZWE5LTgwYzAtZmExYWQ5MDQ4ZWFh");
            put("platform_code", "PT0001");
            put("app_code", "oa#");
            put("access", "");
            put("grd_code", 0);
            put("cls_code", 0);
            put("stu_code", 0);
            put("sub_code", 0);
        }};
        System.out.println(SignUtil.getSign(paraMap));
    }

}