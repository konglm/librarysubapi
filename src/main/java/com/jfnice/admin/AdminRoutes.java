package com.jfnice.admin;

import com.jfnice.admin.asset.AssetController;
import com.jfnice.admin.dict.DictController;
import com.jfnice.core.JFniceBaseRoutes;
import com.jfnice.interceptor.*;

public class AdminRoutes extends JFniceBaseRoutes {

    public AdminRoutes() {
        setProjectName("Api");
        setProjectUrl("/api");
    }

    public void config() {

        addInterceptor(new CrossInterceptor());// 允许跨域
        addInterceptor(new ApiExceptionInterceptor()); // 异常拦截
        addInterceptor(new ApiAuthInterceptor()); // 授权认证
        addInterceptor(new ApiSignInterceptor()); // 验证sign
        addInterceptor(new XssInterceptor()); // XSS拦截

        add("/api/dict", DictController.class); // 字典表
        //多项目公用上传
        add("/api/asset", AssetController.class);
    }

}

