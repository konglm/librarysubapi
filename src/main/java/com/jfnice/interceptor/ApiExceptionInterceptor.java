package com.jfnice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.ErrorMsg;

public class ApiExceptionInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        } catch (ErrorMsg e) { //用户异常
            JFniceBaseController c = (JFniceBaseController) inv.getController();
            c.ajaxMsg(false, e.getMessage(), e.getCode(), e.getData());
        } catch (Throwable t) { //系统异常
            LogKit.error(t.getMessage(), t);
            JFniceBaseController c = (JFniceBaseController) inv.getController();
            c.ajaxMsg(false, "数据异常！", t.getClass().getName(), t.getMessage());
        }
    }

}
