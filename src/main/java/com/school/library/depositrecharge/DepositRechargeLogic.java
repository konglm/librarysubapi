package com.school.library.depositrecharge;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.DepositRecharge;
import com.school.library.kit.CommonKit;

import java.util.Date;
import java.util.List;

public class DepositRechargeLogic {

	@Inject
	private DepositRechargeService service;

	public Page<DepositRecharge> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<DepositRecharge> queryList() {
		return service.queryList("*");
	}

	public Page<Record> depositList(String keywords, String startDate, String endDate, int pageNumber, int pageSize){
		String start_time = CommonKit.dealStartTime(startDate);
		String end_time = CommonKit.dealEndTime(endDate);
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",start_time)
				.set("end_time",end_time)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("DepositRechargeLogic.depositList", kv);
		Page<Record> list = Db.paginate(pageNumber, pageSize, sqlPara);
		for(Record record:list.getList()){
			if(record.getStr("stu_code") != null){
				record.set("user_type","stu");
				record.set("user_type_text","学生");
			}else{
				record.set("user_type","teacher");
				record.set("user_type_text","老师");
			}
		}
		return list;
	}

	public String getTotalDepositAmount(String keywords, String startDate, String endDate){
		String start_time = CommonKit.dealStartTime(startDate);
		String end_time = CommonKit.dealEndTime(endDate);
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",start_time)
				.set("end_time",end_time)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("DepositRechargeLogic.getTotalDepositAmount", kv);
		Record record = Db.findFirst(sqlPara);

		return record.getStr("total_amount");
	}
}