package com.jfnice.admin.setting;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class SettingValidator extends JFniceBaseValidator {

    protected void validate(Controller c) {
        if (isPost()) {
            switch (getActionName()) {
                case "update":
                    validateRequiredString("access", "ERROR_NO_ACCESS", "必须传入access参数！");
                    validateRequiredString("columns", "ERROR_NO_COLUMNS", "必须传入columns参数！");
                    break;
                case "sysUpdate":
                    validateRequiredString("access", "ERROR_NO_ACCESS", "必须传入access参数！");
                    validateRequiredString("columns", "ERROR_NO_COLUMNS", "请输入表头参数！");
                    break;
                default:
            }
        }
    }

}