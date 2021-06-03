package com.school.api.jsy;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.Config;
import com.jfnice.utils.SignUtil;
import com.jfnice.ext.ErrorMsg;

import java.util.Map;

@SuppressWarnings("all")
public class JsyClient {

    private String apiUrl;

    public JsyClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public JsyResponse getResponse(JsyRequest request) {
        String url = apiUrl + request.getAction();
        // 添加参数签名
        Map<String, Object> paraMap = sign(request.paraMap());
        String data = JsonKit.toJson(paraMap);
        //执行请求
        String responseJson = HttpKit.post(url, data, request.getHeaders());

        if (Config.DEV_MODE) {
            System.out.println("\n------------------ " + request.getAction() + " ------------------");
            System.out.println("[" + request.getAction() + "] API REQUEST:");
            System.out.println(data);
            System.out.println("[" + request.getAction() + "] API RESPONSE:");
            System.out.println(responseJson);
            System.out.println("-------------------------------------------------------------------");
        }
        JsyResponse response = JSON.parseObject(responseJson, JsyResponse.class);
        if (!response.isOk()) {
            throw new ErrorMsg(response.getRspTxt(), response.getRspCode());
        }
        return response;
    }

    private Map<String, Object> sign(Map<String, Object> paraMap) {
        try {
            paraMap.put("sign", SignUtil.getSign(paraMap));
            return paraMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
