package com.school.library.search;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class SearchValidator extends JFniceBaseValidator {

	protected void validate(Controller c) {
		if (isPost()) {
			switch (getActionName()) {
				case "add":
					validateRequiredString("name", "ERROR_NO_NAME", "请输入名称！");
					break;
				case "edit":
					validateRequiredString("name", "ERROR_NO_NAME", "请输入名称！");
					break;
				default:
			}
		}
	}

}