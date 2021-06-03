package com.school.library.book;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import com.jfnice.ext.CondPara;
import com.jfnice.model.Book;
import com.jfnice.model.BorrowSetting;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.catalog.CatalogLogic;
import com.school.library.kit.CommonKit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookLogic {

	@Inject
	private BookService service;

	@Inject
	private CatalogLogic catalgLogic;

	@Inject
	private BorrowSettingLogic settingLogic;



	public Page<Book> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<Book> queryList() {
		return service.queryList("*");
	}

	/**
	 * 图书检索
	 */
	public Page<Record> bookSearch(String keywords, String catalogId,String order,String unitCode, int pageNumber, int pageSize ){
		Kv kv = Kv.by("keywords", keywords)
				  .set("unit_code",unitCode)
				  .set("sort",order);
		if (catalogId != null) {
			String ids = "";
			List<Record> allLeafPoint = catalgLogic.getAllLeafPoint(catalogId, unitCode);
			StringBuilder sb = new StringBuilder();
			for (Record catalog : allLeafPoint)
				sb.append(",").append(catalog.getStr("id"));
			if (sb.length() > 0)
				ids = sb.substring(1);

			if ("".equals(ids))
				ids = Long.valueOf(catalogId).toString();
			kv.set("catalogId", ids);
		}
		if(("".equals(keywords)||keywords == null)&&catalogId == null){
			Page<Record> records = new Page<Record>();
			records.setList(new ArrayList<>());
			return records;
		}
        return Db.template("BookLogic.booksearch", kv).paginate(pageNumber, pageSize);
	}

	/**
	 * 书籍详情查询
	 */
	public Kv findByBook(Long id,String unitCode) throws ParseException {
		Kv kv = Kv.by("id", id);
		SqlPara bookSqlPara = Db.getSqlPara("BookLogic.findByBook", kv);
		Record bookDetail = Db.findFirst(bookSqlPara);
		int price = Integer.parseInt(bookDetail.getStr("price"));
		double prices = (double)price/100;
		bookDetail.set("price",prices);
		SqlPara barSql = Db.getSqlPara("BookLogic.barInfoByBook", kv);
		List<Record> records = Db.find(barSql);
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(unitCode);
		Integer borrowDays = borrowSetting.getBorrowDays();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!records.isEmpty()){
			for (Record record:records){
				if (record.getStr("borrow_id") == null){
					record.set("status","在馆");
				}else {
					String borrow_time = record.getStr("borrow_time");
					Date createTime = sdf.parse(borrow_time);
					Date payDate = CommonKit.addDays(createTime, borrowDays);
					String pay_time = sdf.format(payDate);
					record.set("pay_time", pay_time);
					Date nowTime = new Date();
					int borrowDay = (int) ((nowTime.getTime() - createTime.getTime()) / (1000 * 3600 * 24));
					record.set("borrow_day", borrowDay);
					int money = 0;
					if (borrowDay > borrowDays) {
						if (borrowDay - borrowDays > borrowSetting.getFirstBeyondDays()) {
							money = (borrowDay - borrowSetting.getFirstBeyondDays() - borrowDays) * borrowSetting.getSecondBeyondUnitCost()
									+ (borrowSetting.getFirstBeyondDays() * borrowSetting.getFirstBeyondUnitCost())
									+ (borrowDays * borrowSetting.getUnitCost());
						} else {
							money = (borrowDay - borrowDays) * borrowSetting.getFirstBeyondUnitCost()
									+ (borrowDays * borrowSetting.getUnitCost());
						}
						record.set("over_day", borrowDay - borrowDays);
					} else {
						money = borrowDay * borrowSetting.getUnitCost();
						record.set("over_day", 0);
					}

					if (money > borrowSetting.getMaxBorrowCost()) {
						money = borrowSetting.getMaxBorrowCost();
					}
					String moneys = CommonKit.formatMoney(money);
					record.set("cost", moneys);
					String _price = CommonKit.formatMoney(Integer.parseInt(record.getStr("price")));
					record.set("price", _price);
					if ("".equals(record.getStr("stu_code"))) {
						record.set("user_type", "teacher");
					} else {
						record.set("user_type", "stu");
					}
					record.set("status","外借");
				}
			}
		}
		return Kv.by("bookInfo",bookDetail).set("barlist",records);
	}

}