package com.jfnice.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.HttpKit;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.utils.SignUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 参数签名验证拦截器
 *
 * @author jsy
 */
public class ApiSignInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        String bodyJson = HttpKit.readData(inv.getController().getRequest());
        if (StringUtils.isNotEmpty(bodyJson)) {
            // Feature.IgnoreNotMatch 保留值为null的key
            // Feature.OrderedField 属性保持原来的顺序
            JSONObject jsonObject = JSON.parseObject(bodyJson, JSONObject.class, Feature.IgnoreNotMatch, Feature.OrderedField);
            if (jsonObject != null && jsonObject.size() > 0) {
                String sign = Optional.ofNullable(jsonObject.remove("sign")).orElse("").toString();
                String newSign = SignUtil.getSign(jsonObject);
                if (!sign.equals(newSign)) {
                    throw new ErrorMsg("参数签名验证不通过", ResultEnum.URL_PARA_ERROR.getCode());
                }
            }
        }
        inv.invoke();
    }

}
