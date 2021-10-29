package com.school.library.bookbarcode;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.ext.CondPara;
import com.jfnice.model.BookBarCode;

import java.util.List;

public class BookBarCodeLogic {

	@Inject
	private BookBarCodeService service;

	public Page<BookBarCode> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<BookBarCode> queryList() {
		return service.queryList("*");
	}


	/**
	 * 更新索书号
	 * @param bookId
	 * @param checkNo
	 * @param price
	 */
	public void updateCheckNo(long bookId,String checkNo,long price){
		Kv kv = Kv.by("book_id", bookId).set("check_no", checkNo).set("price",price);
		SqlPara barSql = Db.getSqlPara("BookBarCodeLogic.updateCheckNo", kv);
		Db.update(barSql);
	}

	/**
	 * 根据编号查询信息
	 * @param barCode
	 * @param unitCode
	 * @return
	 */
	public BookBarCode findByBarcode(String barCode,String unitCode){
		Kv kv = Kv.by("bar_code", barCode).set("school_code", unitCode);
		SqlPara barSql = Db.getSqlPara("BookBarCodeLogic.findByBarcode", kv);
		return BookBarCode.dao.findFirst(barSql);
	}

	/**
	 * 根据编号逻辑删除
	 */
	public void deleteByBarCode(String barCode,String unitCode){
		Kv kv = Kv.by("bar_code", barCode).set("school_code", unitCode);
		SqlPara barSql = Db.getSqlPara("BookBarCodeLogic.deleteByBarcode", kv);
		Db.update(barSql);
	}

	/**
	 * 注销图书
	 */
	public void writeoffByBarcode(String barCode,String unitCode, String delReason){
		Kv kv = Kv.by("bar_code", barCode).set("school_code", unitCode).set("del_reason", delReason);
		SqlPara barSql = Db.getSqlPara("BookBarCodeLogic.writeoffByBarcode", kv);
		Db.update(barSql);
	}

}