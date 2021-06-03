package com.school.library.bookinventoryitem;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfnice.ext.CondPara;
import com.jfnice.model.BookInventoryItem;

import java.util.List;

public class BookInventoryItemLogic {

	@Inject
	private BookInventoryItemService service;

	public Page<BookInventoryItem> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<BookInventoryItem> queryList() {
		return service.queryList("*");
	}

}