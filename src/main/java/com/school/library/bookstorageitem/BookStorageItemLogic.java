package com.school.library.bookstorageitem;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfnice.ext.CondPara;
import com.jfnice.model.BookStorageItem;

import java.util.List;

public class BookStorageItemLogic {

	@Inject
	private BookStorageItemService service;

	public Page<BookStorageItem> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<BookStorageItem> queryList() {
		return service.queryList("*");
	}

}