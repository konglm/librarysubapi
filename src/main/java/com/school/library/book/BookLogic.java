package com.school.library.book;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.Book;
import com.jfnice.model.BorrowBook;
import com.jfnice.model.BorrowSetting;
import com.school.library.bookstorageitembarcode.BookStorageItemBarCodeService;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.catalog.CatalogLogic;
import com.school.library.kit.CommonKit;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookLogic {

	@Inject
	private BookService service;

	@Inject
	private CatalogLogic catalgLogic;

	@Inject
	private BorrowSettingLogic settingLogic;

	@Inject
	private BookStorageItemBarCodeService itemBarCodeService;



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

	/**
	 * 查询在馆图书
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getBooksIn(int catalogId, String beginTime, String endTime, String keyword, int pageNumber, int pageSize) {
		Page<Record> items = this.service.getBooksIn(CurrentUser.getSchoolCode(), catalogId, beginTime, endTime, keyword, pageNumber, pageSize);
		return items;
	}

	public SXSSFWorkbook createExcelBooksIn(int catalogId, String beginTime, String endTime, String keyword) {

		JSONObject json = new JSONObject();
		String tableTitle = "在馆图书";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("编号", "bar_code");
		headMap.put("书名", "book_name");
		headMap.put("著者", "author");
		headMap.put("金额/元", "price");
		headMap.put("目录名称", "catalog_name");
		headMap.put("索书号", "check_no");
		headMap.put("入库日期", "create_time");
		headMap.put("入库人", "create_user_name");
		headMap.put("借阅次数", "borrow_cnt");

		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("sheet1");
		SXSSFRow row = sheet.createRow(0);
		SXSSFCell cell;
		String key;

		CellStyle cellStyle = wb.createCellStyle();
		Font cellFont = wb.createFont();
		cellFont.setFontName("宋体");
		cellFont.setFontHeightInPoints((short)10);
		cellStyle.setFont(cellFont);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		int rowIndex = 0;
		//第一行
		cell = row.createCell(rowIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(tableTitle);

		//第二行
		row = sheet.createRow(rowIndex++);
		//设置表头
		//列名key值
		List<String> exportableKeyList = new ArrayList<String>();
		//第一列
		int firstCellIndex = 0;
		cell = row.createCell(firstCellIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("序号");
		exportableKeyList.add("seq");
		//设置表头
		int h = 0;
		for (Map.Entry<String, String > entry: headMap.entrySet() ) {

			sheet.setDefaultColumnStyle(h + firstCellIndex, cellStyle);
			cell = row.createCell(h + firstCellIndex);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(entry.getKey());
			exportableKeyList.add(entry.getValue());
			h++;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int pageNumber = 1;
		int pageSize = 500;
		Page<Record> page= this.service.getBooksIn(CurrentUser.getSchoolCode(), catalogId, beginTime, endTime, keyword, pageNumber, pageSize);
		int totalRow = page.getTotalRow();
		int seqIndex = 1;
		while(page.getTotalPage() >= pageNumber){
			List<Record> list = page.getList();
			if(null!= list && !list.isEmpty()){
				for(int i = 0, len = list.size(); i < len; i++){
					Record r = list.get(i);
					row = sheet.createRow(rowIndex++);
					for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
						cell = row.createCell(j);
						cell.setCellStyle(cellStyle);
						key = exportableKeyList.get(j);
						switch ( key ) {
							case "seq":
								cell.setCellValue(seqIndex++);
								break;
							case "price":
								if(r.get(key) == null) {
									cell.setCellValue(0);
								} else {
									cell.setCellValue((double) r.getInt(key) / 100);
								}
								break;
							case "create_time":
								if(r.get(key) == null) {
									cell.setCellValue("");
								} else {
									cell.setCellValue(sdf.format(r.getDate(key)));
								}
								break;
							default:
								cell.setCellValue(r.getStr(key));
						}
					}

				}
			}
			pageNumber = pageNumber + 1;
			page = this.service.getBooksIn(CurrentUser.getSchoolCode(), catalogId, beginTime, endTime, keyword, pageNumber, pageSize);
		}
		return wb;
	}

	/**
	 * 查询在馆图书总数量
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getBooksInCnt(int catalogId, String beginTime, String endTime, String keyword) {
		Record record = this.service.getBooksInCnt(CurrentUser.getSchoolCode(), catalogId, beginTime, endTime, keyword);
		return record.getStr("cnt");
	}

	/**
	 * 查询在馆图书总金额
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getBooksInAmount(int catalogId, String beginTime, String endTime, String keyword) {
		Record record = this.service.getBooksInAmount(CurrentUser.getSchoolCode(), catalogId, beginTime, endTime, keyword);
		return record.getStr("amount");
	}

	/**
	 * 查询外借图书
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getBooksBorrow(int catalogId, int isOverDay, String beginTime, String endTime, String keyword, int pageNumber, int pageSize) {
		Page<Record> records = this.service.getBooksBorrow(CurrentUser.getSchoolCode(), catalogId, isOverDay, beginTime, endTime, keyword, pageNumber, pageSize);
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		Integer borrowDays = 0;
		if(borrowSetting != null) {
			borrowDays = borrowSetting.getBorrowDays();
		}
		for (Record record:records.getList()){
			if ("".equals(record.getStr("stu_code"))) {
				record.set("user_type", "teacher");
			} else {
				record.set("user_type", "stu");
			}
			if((record.get("over_days") != null) && (record.getInt("over_days") > 0)) {
				record.set("is_over_day", 1);
			} else {
				record.set("is_over_day", 0);
			}
			long leftDays = borrowDays - CommonKit.differDays(record.getDate("borrow_time"), new Date());
			if(leftDays < 0) {
				record.set("is_over_day", 1);
				record.set("over_days", Math.abs(leftDays));
				record.set("left_days", 0);
			} else {
				record.set("left_days", leftDays);
			}
		}
		return records;
	}

	public SXSSFWorkbook createExcelBooksBorrow(int catalogId, int isOverDay, String beginTime, String endTime, String keyword) {

		JSONObject json = new JSONObject();
		String tableTitle = "在馆图书";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("编号", "bar_code");
		headMap.put("书名", "book_name");
		headMap.put("著者", "author");
		headMap.put("借阅人", "borrower");
		headMap.put("身份", "user_type");
		headMap.put("部门", "dpt_name");
		headMap.put("年级", "grd_name");
		headMap.put("班级", "cls_name");
		headMap.put("借阅日期", "borrow_time");
		headMap.put("剩余借阅天数", "left_days");
		headMap.put("是否超期", "is_over_day");
		headMap.put("超期天数", "over_days");

		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("sheet1");
		SXSSFRow row = sheet.createRow(0);
		SXSSFCell cell;
		String key;

		CellStyle cellStyle = wb.createCellStyle();
		Font cellFont = wb.createFont();
		cellFont.setFontName("宋体");
		cellFont.setFontHeightInPoints((short)10);
		cellStyle.setFont(cellFont);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		int rowIndex = 0;
		//第一行
		cell = row.createCell(rowIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(tableTitle);

		//第二行
		row = sheet.createRow(rowIndex++);
		//设置表头
		//列名key值
		List<String> exportableKeyList = new ArrayList<String>();
		//第一列
		int firstCellIndex = 0;
		cell = row.createCell(firstCellIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("序号");
		exportableKeyList.add("seq");
		//设置表头
		int h = 0;
		for (Map.Entry<String, String > entry: headMap.entrySet() ) {

			sheet.setDefaultColumnStyle(h + firstCellIndex, cellStyle);
			cell = row.createCell(h + firstCellIndex);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(entry.getKey());
			exportableKeyList.add(entry.getValue());
			h++;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int pageNumber = 1;
		int pageSize = 500;
		Page<Record> page= this.service.getBooksBorrow(CurrentUser.getSchoolCode(), catalogId, isOverDay, beginTime, endTime, keyword, pageNumber, pageSize);
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		Integer borrowDays = borrowSetting.getBorrowDays();
		for (Record record:page.getList()){
			if ("".equals(record.getStr("stu_code"))) {
				record.set("user_type", "教师");
			} else {
				record.set("user_type", "学生");
			}
			if((record.get("over_days") != null) && (record.getInt("over_days") > 0)) {
				record.set("is_over_day", "是");
			} else {
				record.set("is_over_day", "否");
			}
			long leftDays = borrowDays - CommonKit.differDays(record.getDate("borrow_time"), new Date());
			if(leftDays < 0) {
				record.set("is_over_day", "是");
				record.set("over_days", Math.abs(leftDays));
				record.set("left_days", 0);
			} else {
				record.set("left_days", leftDays);
			}
		}
		int totalRow = page.getTotalRow();
		int seqIndex = 1;
		while(page.getTotalPage() >= pageNumber){
			List<Record> list = page.getList();
			if(null!= list && !list.isEmpty()){
				for(int i = 0, len = list.size(); i < len; i++){
					Record r = list.get(i);
					row = sheet.createRow(rowIndex++);
					for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
						cell = row.createCell(j);
						cell.setCellStyle(cellStyle);
						key = exportableKeyList.get(j);
						switch ( key ) {
							case "seq":
								cell.setCellValue(seqIndex++);
								break;
							case "borrow_time":
								if(r.get(key) == null) {
									cell.setCellValue("");
								} else {
									cell.setCellValue(sdf.format(r.getDate(key)));
								}
								break;
							default:
								cell.setCellValue(r.getStr(key));
						}
					}

				}
			}
			pageNumber = pageNumber + 1;
			page = this.service.getBooksBorrow(CurrentUser.getSchoolCode(), catalogId, isOverDay, beginTime, endTime, keyword, pageNumber, pageSize);
		}
		return wb;
	}

	/**
	 * 查询外借图书总数量
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getBooksBorrowCnt(int catalogId, int isOverDay, String beginTime, String endTime, String keyword) {
		Record record = this.service.getBooksBorrowCnt(CurrentUser.getSchoolCode(), catalogId, isOverDay, beginTime, endTime, keyword);
		return record.getStr("cnt");
	}

	/**
	 * 查询外借图书总金额
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getBooksBorrowAmount(int catalogId, int isOverDay, String beginTime, String endTime, String keyword) {
		Record record = this.service.getBooksBorrowAmount(CurrentUser.getSchoolCode(), catalogId, isOverDay, beginTime, endTime, keyword);
		return record.getStr("amount");
	}

	/**
	 * 通过条形码获取图书
	 * @param barCode
	 * @return
	 */
	public JSONObject getBookInfoByBar(String barCode, int pageNumber, int pageSize) {
		JSONObject data = new JSONObject();
		Record bookInfo = this.service.getBookInfoByBar(CurrentUser.getSchoolCode(), barCode);
		if(bookInfo != null) {
			data.put("bar_code", bookInfo.getStr("bar_code"));
			data.put("check_no", bookInfo.getStr("check_no"));
			data.put("book_name", bookInfo.getStr("book_name"));
			data.put("book_img_url", bookInfo.getStr("book_img_url"));
			data.put("author", bookInfo.getStr("author"));
			data.put("publisher", bookInfo.getStr("publisher"));
			data.put("publish_date", bookInfo.getStr("publish_date"));
			data.put("price", bookInfo.getStr("price"));
			data.put("catalog_name", bookInfo.getStr("catalog_name"));
			data.put("create_time", bookInfo.getStr("create_time"));
			data.put("create_user_name", bookInfo.getStr("create_user_name"));
		} else {
			data.put("bar_code", "");
			data.put("check_no", "");
			data.put("book_name", "");
			data.put("book_img_url", "");
			data.put("author","");
			data.put("publisher", "");
			data.put("publish_date", "");
			data.put("price", "");
			data.put("catalog_name", "");
			data.put("create_time", "");
			data.put("create_user_name", "");
		}
		Page<Record> borrowList = this.service.getBookBorrowList(CurrentUser.getSchoolCode(), barCode, pageNumber, pageSize);
		for (Record record:borrowList.getList()){
			if ("".equals(record.getStr("return_time"))) {
				long borrowDays = CommonKit.differDays(record.getDate("borrow_time"), new Date());
				record.set("borrow_days", borrowDays);
			} else {
				long borrowDays = CommonKit.differDays(record.getDate("borrow_time"), record.getDate("return_time"));
				record.set("borrow_days", borrowDays);
			}
		}
		data.put("list", borrowList);
		return data;
	}

}