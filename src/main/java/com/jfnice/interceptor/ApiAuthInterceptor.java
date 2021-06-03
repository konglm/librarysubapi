package com.jfnice.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.HttpKit;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.utils.ThreadLocalUtil;
import com.school.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;

/**
 * 必传参数验证拦截器
 *
 * @author jsy
 */
public class ApiAuthInterceptor implements Interceptor {

    /**
     * 必须有的参数
     */
    private final String[] paras = new String[]{"access_token", "platform_code", "app_code", "unit_code", "index_code"};

    @Override
    public void intercept(Invocation inv) {
        JFniceBaseController c = (JFniceBaseController) inv.getController();
        // 验证必传参数
        String bodyJson = HttpKit.readData(inv.getController().getRequest());
        if (StringUtils.isNotEmpty(bodyJson)) {
            JSONObject jsonObject = JSON.parseObject(bodyJson, JSONObject.class, Feature.IgnoreNotMatch);
            if (jsonObject != null && jsonObject.size() > 0) {
                for (String p : paras) {
                    if (!jsonObject.containsKey(p)) {
                        throw new ErrorMsg("请传入参数" + p + "！", ResultEnum.URL_PARA_ERROR.getCode());
                    }
                }
            }
        }
        LoginUser loginUser = JSON.parseObject(bodyJson, LoginUser.class);
        ThreadLocalUtil.getInstance().bind(loginUser);
        try {
            inv.invoke();
        } finally {
            ThreadLocalUtil.getInstance().remove();
        }
    }

}
