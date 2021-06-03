package com.school.demo;

import com.jfnice.core.JFniceBaseRoutes;
import com.jfnice.interceptor.*;

public class DemoRoutes extends JFniceBaseRoutes {

    public DemoRoutes() {
        setProjectName("Api");
        setProjectUrl("/api");
    }

    public void config() {

        addInterceptor(new CrossInterceptor());// 允许跨域
        addInterceptor(new ApiExceptionInterceptor()); // 异常拦截
        addInterceptor(new ApiAuthInterceptor()); // 登陆拦截
        addInterceptor(new ClearCacheInterceptor()); // 清除缓存
        addInterceptor(new JsyShiroInterceptor()); // 授权认证
        addInterceptor(new ApiSignInterceptor()); // 验证sign
        addInterceptor(new XssInterceptor()); // XSS拦截

        // 授权接口示例
        add("/", IndexController.class);
    }

}

