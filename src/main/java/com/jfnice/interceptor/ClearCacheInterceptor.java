package com.jfnice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CurrentUser;
import com.jfnice.j2cache.J2CacheKit;
import com.jfnice.utils.ThreadLocalUtil;
import com.school.api.map.ApiMapInit;

public class ClearCacheInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        String accessToken = CurrentUser.getAccessToken();
        // 判断access_token是不是新用户，如果为新用户，去除缓存
        String cacheToken = J2CacheKit.get("login_access_token", accessToken);
        if (accessToken != null && cacheToken == null) {
            ApiMapInit.clear();
            J2CacheKit.put("login_access_token", accessToken, accessToken);
        }
        inv.invoke();
    }

}
