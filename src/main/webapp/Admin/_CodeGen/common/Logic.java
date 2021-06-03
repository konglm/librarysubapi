package com.jfnice._gen.#(lowercaseModelName);

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfnice.ext.CondPara;
import com.jfnice.model.#(modelName);

import java.util.List;

public class #(modelName)Logic {

	@Inject
	private #(modelName)Service service;

	public Page<#(modelName)> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<#(modelName)> queryList() {
		return service.queryList("*");
	}

}