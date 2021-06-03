package com.jfnice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.annotation.ShiroClear;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.Logical;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.school.api.gx.PtApi;

/**
 * 验证权限及token拦截器
 *
 * @author jsy
 */
public class JsyShiroInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        try {
            shiroValidate(inv);
        } catch (ErrorMsg e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        inv.invoke();
    }

    private void shiroValidate(Invocation inv) {
        ShiroClear classShiroClear = inv.getMethod().getDeclaringClass().getAnnotation(ShiroClear.class);
        ShiroClear methodShiroClear = inv.getMethod().getAnnotation(ShiroClear.class);
        // class是不是JsyPermissions注解
        JsyPermissions classJsyPermissions = inv.getMethod().getDeclaringClass().getAnnotation(JsyPermissions.class);
        // method是不是JsyPermissions注解
        JsyPermissions methodJsyPermissions = inv.getMethod().getAnnotation(JsyPermissions.class);

        // 如果没有设置ShiroClear
        if (classShiroClear == null && methodShiroClear == null) {
            JFniceBaseController c = (JFniceBaseController) inv.getController();
            // 权限符数组
            OpCodeEnum[] opCodeEnums;
            Object grdCode;
            Object clsCode;
            Object subCode;
            Object stuCode;
            Logical logical;
            boolean pass = false;
            if (classJsyPermissions != null) {
                // 注解在类上
                opCodeEnums = classJsyPermissions.value();
                grdCode = "0";
                clsCode = "0";
                subCode = "0";
                stuCode = "0";
                logical = classJsyPermissions.logical();
                pass = classJsyPermissions.pass();
            } else if (methodJsyPermissions != null) {
                // 注解在方法上
                opCodeEnums = methodJsyPermissions.value();
                CondPara condPara = methodJsyPermissions.condPara() ? JsonKit.parse(c.getRawData(), CondPara.class) : null;
                grdCode = getValue(methodJsyPermissions.grdCodeParaName(), condPara, c);
                clsCode = getValue(methodJsyPermissions.clsCodeParaName(), condPara, c);
                subCode = getValue(methodJsyPermissions.subCodeParaName(), condPara, c);
                stuCode = getValue(methodJsyPermissions.stuCodeParaName(), condPara, c);
                logical = methodJsyPermissions.logical();
                pass = methodJsyPermissions.pass();
            } else {
                // 如果没有JsyPermissions注解，需要验证token的有效性
                PtApi.verifyToken();
                return;
            }
            if(pass){
                return;
            }
            String res = PtApi.getPermissionByPositionList(opCodeEnums, grdCode, clsCode, subCode, stuCode);
            boolean isPermitted = StrKit.notBlank(res) && (logical.equals(Logical.AND) ? !res.contains("0") : res.contains("1"));
            if (!isPermitted) {
                throw new ErrorMsg(ResultEnum.AUTHORITY_ERROR);
            }
        }
    }

    private Object getValue(String paraName, CondPara condPara, JFniceBaseController c) {
        // 如果参数名为 ignore 表示不需要判断
        if ("ignore".equals(paraName)) {
            return "0";
        }
        if (condPara != null) {
            return condPara.get(paraName);
        }
        return c.getPara(paraName);
    }

}
