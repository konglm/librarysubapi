package com.school.api.gx;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfnice.commons.Config;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.utils.SignUtil;

import java.util.Map;

@SuppressWarnings("all")
public class GxClient {

    private String apiUrl;

    public GxClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public GxResponse getResponse(GxRequest request) {
        String url = apiUrl + request.getAction();
        // 添加参数签名
        Map<String, Object> paraMap = sign(request.paraMap());
        String data = JsonKit.toJson(paraMap);
        //执行请求
        String responseJson = HttpKit.post(url, data, request.getHeaders());

        if (Config.DEV_MODE) {
            System.out.println("\n------------------ " + url + " ------------------");
            System.out.println("[" + request.getAction() + "] API REQUEST:");
            System.out.println(data);
            System.out.println("[" + request.getAction() + "] API RESPONSE:");
            System.out.println(responseJson);
            System.out.println("-------------------------------------------------------------------");
        }
        GxResponse response = JSON.parseObject(responseJson, GxResponse.class);
        if (!response.isOk()) {
            throw new ErrorMsg(response.getMsg(), response.getCode(), response.getData());
        }
        return response;
    }

    private Map<String, Object> sign(Map<String, Object> paraMap) {
        String sign = SignUtil.getSign(paraMap);
        if (sign != null) {
            paraMap.put("sign", sign);
        }
        return paraMap;
    }

}
