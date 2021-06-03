package com.jfnice.qiniu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Api {

    private static final String ApiUrl = PropKit.get("apiUrl").trim();
    public static final String GetUpLoadToken = ApiUrl + "/Api/QiNiu/GetUpLoadToKen";
    public static final String GetAccess = ApiUrl + "/Api/QiNiu/GetAccess";
    private static final String AppId = PropKit.get("appId").trim();
    private static final String Bucket = PropKit.get("bucketName").trim();
    private static final String SecretKey = PropKit.get("secretKey").trim();

    /**
     * 调用接口给link添加token
     */
    public static String getLink(String link) {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("AppID", AppId);
        String[] urls = new String[]{link};
        paraMap.put("Param", DesUtil.jsencrypt(SecretKey, JsonKit.toJson(urls)));
        Resp resp = Auth.request(GetAccess, paraMap);
        if (resp.isOk()) {
            JSONArray jsonArray = JSON.parseArray(resp.getData());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject.get("Value").toString();
        }
        return null;
    }

    /**
     * 获取上传token
     * @param key
     * @return
     */
    public static String getUploadToken(String key) {
        Map<String, String> paraMap = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        paraMap.put("AppID", AppId);

        params.put("Bucket", Bucket);
        params.put("Key", key);
        params.put("Pops", "");
        params.put("NotifyUrl", "");

        paraMap.put("Param", DesUtil.jsencrypt(SecretKey, JsonKit.toJson(params)));
        Resp resp = Auth.request(GetUpLoadToken, paraMap);
        return resp.isOk() ? resp.getData() : null;
    }

    /**
     * 获取批量上传token
     * @param keys
     * @return
     */
    public static String getBatchUploadToken(String[] keys) {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("AppID", AppId);

        if (keys.length == 0) {
            return null;
        }
        List<Map<String, String>> paramList = new ArrayList<>();

        for (String key : keys) {
            Map<String, String> params = new HashMap<>();
            params.put("Bucket", Bucket);
            params.put("Key", key);
            params.put("Pops", "");
            params.put("NotifyUrl", "");
            paramList.add(params);
        }

        paraMap.put("Param", DesUtil.jsencrypt(SecretKey, JsonKit.toJson(paramList)));
        Resp resp = Auth.request(GetUpLoadToken, paraMap);
        return resp.isOk() ? resp.getData() : null;
    }
}
