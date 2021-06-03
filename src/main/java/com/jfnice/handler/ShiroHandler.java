package com.jfnice.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HttpKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ShiroHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            HttpServletRequestExtend req = new HttpServletRequestExtend(request);
            JSONObject jsonObject = JSON.parseObject(HttpKit.readData(req));
            if (jsonObject != null) {
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    req.setParameter(entry.getKey(), entry.getValue());
                }
            }
            request = req;
        }

        next.handle(target, request, response, isHandled);
    }

}
