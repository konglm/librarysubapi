package com.school.library.catalog;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class CatalogValidator extends JFniceBaseValidator {

	@Override
	protected void validate(Controller c) {
		if (isPost()) {
			switch (getActionName()) {
				case "add":
					validateString("catalog_no", 1, 8, "ERROR_NO_NO", "分类号不能为空，且最长8个字符！");
					validateString("catalog_name", 1, 32, "ERROR_NO_NAME", "分类名称不能为空，且最长32个字符！");
					break;
				case "edit":
					validateString("catalog_no", 1, 8, "ERROR_NO_NO", "分类号不能为空，且最长8个字符！");
					validateString("catalog_name", 1, 32, "ERROR_NO_NAME", "分类名称不能为空，且最长32个字符！");
					break;
				default:
			}
		}
	}

}