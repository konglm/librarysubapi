package com.jfnice.core;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

import java.util.Enumeration;

public abstract class JFniceBaseValidator extends Validator {

    /**
     * 错误代码前缀
     */
    private final String prefix = "error_";
    private static String[] specialCharacterArray = {
            " ", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "=", "+",
            "[", "]", "{", "}", "\\", "|", ";", ":", "'", "\"", ",", "<", ">", "/", "?",
            "　", "＠", "＃", "＆", "，", "。", "《", "》", "？", "！"  // 全角字符
    };

    /**
     * 构造函数。短路模式，即任意一个验证不通过则立即返回错误信息
     *
     * @author JFnice
     */
    public JFniceBaseValidator() {
        setShortCircuit(true);
    }

    @Override
    protected void addError(String errorKey, String errorMessage) {
        // 添加前缀，以便与其他参数区别
        errorKey = prefix + errorKey;
        super.addError(errorKey, errorMessage);
    }

    /**
     * 实现Validator基类的handleError方法
     *
     * @author JFnice
     */
    @Override
    protected void handleError(Controller c) {
        checkError();
    }

    /**
     * 获取JFniceBaseController
     *
     * @return JFniceBaseController
     * @author JFnice
     */
    protected JFniceBaseController getJFniceBaseController() {
        return (JFniceBaseController) getController();
    }

    /**
     * 获取当前Action名称
     *
     * @return String
     * @author JFnice
     */
    protected String getActionName() {
        return getActionMethod().getName();
    }

    /**
     * 是否为POST提交方式
     *
     * @return boolean
     * @author JFnice
     */
    protected boolean isPost() {
        return getJFniceBaseController().isPost();
    }

    /**
     * 是否为AJAX请求方式
     *
     * @return boolean
     * @author JFnice
     */
    protected boolean isAjax() {
        return getJFniceBaseController().isAjax();
    }

    /**
     * 检验验证错误信息。如果是AJAX请求方式则返回JSON错误数据，否则跳转错误处理页面
     *
     * @author JFnice
     */
    protected void checkError() {
        JFniceBaseController c = getJFniceBaseController();
        Enumeration<String> e = c.getAttrNames();
        while (e.hasMoreElements()) {
            String code = e.nextElement();
            // 只获取有前缀打头的参数信息
            if (code.startsWith(prefix)) {
                String msg = c.getAttrForStr(code);
                code = code.replace(prefix, "");
                if (isAjax()) {
                    c.fail(msg, code);
                } else {
                    c.showErrorView(code, msg);
                }
                break;//短路模式
            }
        }
    }

    /**
     * 检验字段是否包含特殊字符
     *
     * @param field     字段名称
     * @param errorKey  错误代码
     * @param fieldName 字段名称对应的中文名称
     * @author JFnice
     */
    protected void validateSpecialCharacter(String field, String errorKey, String fieldName) {
        JFniceBaseController c = getJFniceBaseController();
        for (String s : specialCharacterArray) {
            if (StrKit.notBlank(c.getPara(field)) && c.getPara(field).contains(s)) {
                addError(errorKey, StrKit.join(new String[]{fieldName, "不能包含特殊字符：\"", s, "\""}));
                break;//短路模式
            }
        }
    }

}
