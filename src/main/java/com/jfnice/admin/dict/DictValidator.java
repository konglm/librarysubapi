package com.jfnice.admin.dict;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class DictValidator extends JFniceBaseValidator {

    protected void validate(Controller c) {
        if (isPost()) {
            switch (getActionName()) {
                case "add":
                    validateRequiredString("tag", "ERROR_NO_TAG", "请输入标签！");
                    validateRequiredString("k", "ERROR_NO_KEY", "请输入键名！");
                    validateRequiredString("v", "ERROR_NO_VALUE", "请输入键值！");
                    validateRequiredString("label", "ERROR_NO_LABEL", "请输入标记！");
                    break;
                case "edit":
                    validateRequiredString("tag", "ERROR_NO_TAG", "请输入标签！");
                    validateRequiredString("k", "ERROR_NO_KEY", "请输入键名！");
                    validateRequiredString("v", "ERROR_NO_VALUE", "请输入键值！");
                    validateRequiredString("label", "ERROR_NO_LABEL", "请输入标记！");
                    break;
                default:
            }
        }
    }

}