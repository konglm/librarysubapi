package com.jfnice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.kit.PropKit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名工具类
 *
 * @author jsy
 */
public class SignUtil {

    public static String getSign(Map<String, Object> paraMap) {
        String sign = null;

        SecretKeySpec secretKeySpec = new SecretKeySpec(PropKit.get("apiSecretKey").getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException();
        }

        String key;
        Object value;
        StringBuilder sb = new StringBuilder();
        TreeMap<String, Object> paraTreeMap = new TreeMap<>(paraMap);
        for (Map.Entry<String, Object> entry : paraTreeMap.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value == null) {
                value = "";
            } else if (!(value instanceof String)) {
                value = JSON.toJSONString(value, SerializerFeature.WriteMapNullValue);
            }
            sb.append(key).append("=").append(value).append("&");
        }

        if (sb.length() > 0) {
            String queryParas = sb.substring(0, sb.length() - 1);
            sign = Base64.getEncoder().encodeToString(mac.doFinal(queryParas.getBytes(StandardCharsets.UTF_8)));
        }
        return sign;
    }

}
