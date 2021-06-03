package com.jfnice.commons;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * 读取配置类
 */
public class Config {

    private static final Prop prop = PropKit.use("config.properties");

    public static final String PLATFORM_CODE = prop.get("platformCode", "").trim();
    // 总部API
    public static final String JSY_API_URL = prop.get("jsyApiUrl", "").trim();
    // 平台API
    public static final String PT_SKIN_API_URL = prop.get("ptSkinApiUrl", "").trim();
    public static final String PT_SUB_API_URL = prop.get("ptSubApiUrl", "").trim();
    // 人事API
    public static final String RS_SUB_API_URL = prop.get("rsSubApiUrl", "").trim();

    public static final String SECRET_KEY = prop.get("apiSecretKey", "").trim();
    public static final String APP_ID = prop.get("apiAppId", "").trim();
    public static final boolean DEV_MODE = prop.getBoolean("apiDevMode", false);

}
