package com.school.library.bookstorage;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class BookStorageValidator extends JFniceBaseValidator {

	@Override
	protected void validate(Controller c) {
		if (isPost()) {
			switch (getActionName()) {
				case "saveItem":
					validateLong("book_storage_id", "ERROR_STORAGE_ID", "入库事件id不能为空");
					validateLong("catalog_id", "ERROR_CATALOG_ID", "图书目录不能为空");
					validateString("book_name", 1, 64, "ERROR_NO_NAME", "书名不能为空，且最长64个字符！");
					validateString("author", 1, 32,"ERROR_NO_AUTHOR", "作者不能为空，且最长32个字符！");
					validateString("publisher",1, 64, "ERROR_NO_PUBLISHER", "出版社不能为空，且最长64个字符！");
					validateRequiredString("publish_date", "ERROR_NO_DATE", "出版日期不能为空！");
					validateInteger("price", 1, 21474836,"ERROR_NO_PRICE", "价格不能为空，最小值为1，最大值为21474836！");
					validateInteger("book_num", 1, 21474836,"ERROR_NO_NUM", "数量必须为整数，且最小值为1，最大值为21474836！");
					validateString("book_img_url", 0, 128, "ERROR_IMG_LENGTH", "图片URL最大长度128个字符");
					break;
				case "edit":
					//validateRequiredString("name", "ERROR_NO_NAME", "请输入名称！");
					break;
				case "page":
					validateInteger("page_number", 1, Integer.MAX_VALUE, "ERROR_NO_PAGE_NUMBER", "页码不能为空,且最小值为1！");
					validateInteger("page_size", 1, Integer.MAX_VALUE,"ERROR_NO_PAGE_SIZE", "页面大小不能为空，且最小值为1！");
					break;
				default:
			}
		}
	}

}