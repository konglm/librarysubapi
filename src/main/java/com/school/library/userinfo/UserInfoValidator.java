package com.school.library.userinfo;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class UserInfoValidator extends JFniceBaseValidator {

	protected void validate(Controller c) {
		if (isPost()) {
			switch (getActionName()) {
				case "page":
					validateInteger("page_number", 1, Integer.MAX_VALUE, "ERROR_NO_PAGE_NUMBER", "页码不能为空,且最小值为1！");
					validateInteger("page_size", 1, Integer.MAX_VALUE,"ERROR_NO_PAGE_SIZE", "页面大小不能为空，且最小值为1！");
					break;
				default:
			}
		}
	}

}