package com.jfnice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfnice.core.JFniceBaseController;

/** 允许跨域 */
public class CrossInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        JFniceBaseController c = (JFniceBaseController) inv.getController();
        c.getResponse().setHeader("Access-Control-Allow-Origin", "*");
        c.getResponse().setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        c.getResponse().setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, csrf-token");
        c.getResponse().setHeader("Access-Control-Allow-Credentials", "true");
        // c.getResponse().setHeader("Content-Type", "application/json;
        // charset=UTF-8");

        if (c.getRequest().getMethod().equals("OPTIONS")) {// options 返回200
            c.renderJson();
            return;
        }

        inv.invoke();
    }

}
