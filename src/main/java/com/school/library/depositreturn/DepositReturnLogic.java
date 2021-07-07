package com.school.library.depositreturn;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.DepositReturn;

import java.util.List;

public class DepositReturnLogic {

	@Inject
	private DepositReturnService service;

	public Page<Record> depositList(String keywords, String startTime, String endTime, int pageNumber, int pageSize) {
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",startTime)
				.set("end_time",endTime)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("DepositReturnLogic.depositList", kv);
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

	public String getTotalDepositAmount(String keywords, String startTime, String endTime) {
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",startTime)
				.set("end_time",endTime)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("DepositReturnLogic.getTotalDepositAmount", kv);
		Record record = Db.findFirst(sqlPara);

		return record.getStr("total_amount");
	}

}