package com.school.library.search;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.Search;

import java.util.Date;
import java.util.List;

public class SearchLogic {

	@Inject
	private SearchService service;

	public Page<Search> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<Search> queryList() {
		return service.queryList("*");
	}

	/**
	 * 记录搜索
	 */
	public void recordSearch(String keywords){
		Search search = new Search();
		search.setCreateTime(new Date());
		search.setCreateUserCode(CurrentUser.getUserCode());
		search.setCreateUserName(CurrentUser.getUserName());
		search.setDel(false);
		search.setSchoolCode(CurrentUser.getSchoolCode());
		search.setKeyWord(keywords);
		search.save();
	}

}