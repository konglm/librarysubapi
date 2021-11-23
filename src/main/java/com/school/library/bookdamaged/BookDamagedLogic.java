package com.school.library.bookdamaged;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.*;
import com.school.library.bookbarcode.BookBarCodeLogic;
import com.school.library.borrowbook.BorrowBookLogic;
import com.school.library.borrowbook.BorrowBookService;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.kit.CommonKit;
import com.school.library.userinfo.UserInfoLogic;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.*;

public class BookDamagedLogic {

	@Inject
	private BookDamagedService service;

	@Inject
	private BorrowBookService borrowService;

	@Inject
	private BookBarCodeLogic bookBarCodeLogic;

	@Inject
	private BorrowBookLogic borrowBookLogic;

	@Inject
	private UserInfoLogic userInfoLogic;

	@Inject
	private BorrowSettingLogic settingLogic;

	public Page<BookDamaged> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<BookDamaged> queryList() {
		return service.queryList("*");
	}

	/**
	 * 登记破损时bar表与borrow表的变化
	 * @param barCode
	 * @param unitCode
	 * @param borrowId
	 * @param cost
	 * @param bookStatus
	 */
	public void recordDamage(String barCode,String unitCode,int borrowId,int cost,int bookStatus,BookDamaged bookDamaged){
        //登记图书状态至书库
//        BookBarCode bookBarCode = bookBarCodeLogic.findByBarcode(barCode, unitCode);
//        bookBarCode.setStatus(bookStatus);
//        bookBarCode.update();

		BorrowBook borrowBook = borrowService.queryById(borrowId);
		borrowBook.setBookStatus(bookStatus);
		borrowBook.setUpdateTime(new Date());
		borrowBook.setUpdateUserCode(CurrentUser.getUserCode());
		borrowBook.setReturnTime(new Date());
		borrowBook.setReturnStatus(1);
		Integer overTimeCost = borrowBookLogic.cost(unitCode, borrowBook.getBorrowTime());
//		borrowBook.setDeductions(cost+overTimeCost);
		borrowBook.setDeductions(overTimeCost);
		borrowBook.setOverDays(borrowBookLogic.overDays(borrowBook.getBorrowTime()));
		String userCode = null;
		String userType = null;
		if(borrowBook.getStuCode() == null){
			userCode = borrowBook.getUserCode();
			userType = "teacher";
		}else{
			userCode = borrowBook.getStuCode();
			userType = "stu";
		}
		UserInfo userInfo = userInfoLogic.findByUserCode(unitCode, userCode, userType);
		int deposit = userInfo.getDeposit();
//		if(deposit -cost - overTimeCost<0 ){
//			throw new ErrorMsg("押金不足！");
//		}
		borrowBook.update();
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(unitCode);
		Short depositAuditFlag = borrowSetting.getDepositAuditFlag();

		if (depositAuditFlag==0){
			deposit = deposit -cost - overTimeCost;
			bookDamaged.setJudge(-1);
		}else{
			deposit = deposit - overTimeCost;
			bookDamaged.setJudge(0);
		}
		userInfo.setDeposit(deposit);
		userInfo.update();
		bookDamaged.setLastStatus(0);
		bookDamaged.setRecorder(CurrentUser.getUserName());
		bookDamaged.setRecorderCode(CurrentUser.getUserCode());
		bookDamaged.setDeductions(cost);
		bookDamaged.setRecordTime(new Date());
		bookDamaged.save();

	}

    /**
     * 修复图书
     * @param barCode
     * @param unitCode
     * @param lastStatus
     * @param id
     */
	public void repairBook(String barCode,String unitCode,int lastStatus,long id){
		BookDamaged bookDamaged = service.queryById(id);
		bookDamaged.setLastStatus(lastStatus);
		if (lastStatus == 1){
			bookDamaged.setRepairer(CurrentUser.getUserName());
			bookDamaged.setRepairerCode(CurrentUser.getUserCode());
			bookDamaged.setRepairTime(new Date());
		}
		bookDamaged.update();
        //登记丢失和损毁状态至书库
		BookBarCode barcode = bookBarCodeLogic.findByBarcode(barCode, unitCode);
		barcode.setStatus(lastStatus);
		barcode.update();

	}

	/**
	 * 破损列表
	 * @param unitCode
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param repairType
	 * @param bookStatus
	 * @return
	 */
	public Page<BookDamaged> damagedList(String unitCode,int pageNumber,int pageSize,String keywords,String repairType,String bookStatus){
		Kv kv = Kv.by("school_code", unitCode)
				.set("keywords",keywords)
				.set("repair_type",repairType)
				.set("book_status",bookStatus);
		SqlPara sqlPara = Db.getSqlPara("BookDamagedLogic.queryBysch", kv);
		return BookDamaged.dao.paginate(pageNumber,pageSize,sqlPara);
	}

	public SXSSFWorkbook createExcelDamagedList(String unitCode,String keywords,String repairType,String bookStatus) {

		JSONObject json = new JSONObject();
		String tableTitle = "问题图书";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("编号", "bar_code");
		headMap.put("书名", "book_name");
		headMap.put("著者", "author");
		headMap.put("图书情况", "book_status");
		headMap.put("记录人", "recorder");
		headMap.put("记录日期", "record_time");
		headMap.put("维修状态", "last_status");
		headMap.put("维修人", "repairer");
		headMap.put("维修时间", "repair_time");

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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int pageNumber = 1;
		int pageSize = 500;
		Page<BookDamaged> page= this.damagedList(unitCode, pageNumber, pageSize,keywords,repairType,bookStatus);
		for (BookDamaged record:page.getList()){
			if ("2".equals(record.getStr("book_status"))) {
				record.set("book_status", "破损");
				if("1".equals(record.getStr("last_status"))) {
					record.set("last_status", "已维修");
				} else {
					record.set("last_status", "未维修");
				}
			} else if ("3".equals(record.getStr("book_status"))) {
				record.set("book_status", "损毁");
				record.set("last_status", "");
			} else if ("6".equals(record.getStr("book_status"))) {
				record.set("book_status", "注销");
				record.set("last_status", "");
			} else {
				record.set("book_status", "丢失");
				record.set("last_status", "");
			}
		}
		int totalRow = page.getTotalRow();
		int seqIndex = 1;
		while(page.getTotalPage() >= pageNumber){
			List<BookDamaged> list = page.getList();
			if(null!= list && !list.isEmpty()){
				for(int i = 0, len = list.size(); i < len; i++){
					BookDamaged r = list.get(i);
					row = sheet.createRow(rowIndex++);
					for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
						cell = row.createCell(j);
						cell.setCellStyle(cellStyle);
						key = exportableKeyList.get(j);
						switch ( key ) {
							case "seq":
								cell.setCellValue(seqIndex++);
								break;
							case "record_time":
							case "repair_time":
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
			page = this.damagedList(unitCode, pageNumber, pageSize,keywords,repairType,bookStatus);
		}
		return wb;
	}

	/**
	 * 破损总数
	 * @param unitCode
	 * @param keywords
	 * @param repairType
	 * @param bookStatus
	 * @return
	 */
	public String damagedTotalCnt(String unitCode, String keywords,String repairType,String bookStatus){
		Kv kv = Kv.by("school_code", unitCode)
				.set("keywords",keywords)
				.set("repair_type",repairType)
				.set("book_status",bookStatus);
		SqlPara sqlPara = Db.getSqlPara("BookDamagedLogic.damagedTotalCnt", kv);
		return BookDamaged.dao.findFirst(sqlPara).getStr("total_cnt");
	}

	/**
	 * 破损总金额
	 * @param unitCode
	 * @param keywords
	 * @param repairType
	 * @param bookStatus
	 * @return
	 */
	public String damagedTotalAmount(String unitCode, String keywords,String repairType,String bookStatus){
		Kv kv = Kv.by("school_code", unitCode)
				.set("keywords",keywords)
				.set("repair_type",repairType)
				.set("book_status",bookStatus);
		SqlPara sqlPara = Db.getSqlPara("BookDamagedLogic.damagedTotalAmount", kv);
		return BookDamaged.dao.findFirst(sqlPara).getStr("total_amount");
	}

	public Record damagedDetail(Long id){
		Kv kv = Kv.by("id", id);
		SqlPara sqlPara = Db.getSqlPara("BookDamagedLogic.queryDetail", kv);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 破损审核列表
	 */
	public Page<Record> checkList(String judge,String bookStatus,String startTime,String endTime,String unitCode,int pageNumber,int pageSize){
		String start_time = CommonKit.dealStartTime(startTime);
		String end_time = CommonKit.dealEndTime(endTime);
		Kv kv = Kv.by("judge", judge)
				.set("book_status",bookStatus)
				.set("unit_code",unitCode)
				.set("start_time",start_time)
				.set("end_time",end_time);
		SqlPara sqlPara = Db.getSqlPara("BookDamagedLogic.queryCheckList", kv);
        return Db.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 更改押金
	 * @param newCost
	 * @param borrowId
	 * @param id
	 */
	public void changeDeduction(int newCost,long borrowId ,long id){
		//BorrowBook borrowBook = borrowService.queryById(borrowId);
		BookDamaged bookDamaged = service.queryById(id);
		//Integer totalcost = borrowBook.getDeductions();
		//Integer oldcost = bookDamaged.getDeductions();
		//借书表的金额在最后一步审核才进行处理
		//borrowBook.setDeductions(totalcost-oldcost+newCost);
		//borrowBook.update();
		bookDamaged.setDeductions(newCost);
		bookDamaged.update();
	}

	/**
	 * 破损押金审核
	 * @param id
	 * @param judge
	 * @param unitCode
	 * @param userType
	 * @param userCode
	 * @param stuCode
	 * @param cost
	 */
	public void judge(long id,int judge,String unitCode,String userType,String userCode,String stuCode,int cost){

		BookDamaged bookDamaged = service.queryById(id);
		if(judge == 1){
			BorrowBook borrowBook = borrowService.queryById(bookDamaged.getBorrowId());
			borrowBook.setUpdateTime(new Date());
			borrowBook.setUpdateUserCode(CurrentUser.getUserCode());
			borrowBook.setDeductions(cost + borrowBook.getDeductions());
			borrowBook.update();
			if(userType.equals("stu")){
				userCode = stuCode;
			}
			UserInfo userInfo = userInfoLogic.findByUserCode(unitCode, userCode, userType);
			userInfo.setDeposit(userInfo.getDeposit()-cost);
			userInfo.update();
		}

		bookDamaged.setJudge(judge);
		bookDamaged.setJudger(CurrentUser.getUserName());
		bookDamaged.setJudgerCode(CurrentUser.getUserCode());
		bookDamaged.update();


	}

	public void writeOffBook(String unitCode, BookDamaged bookDamaged){
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(unitCode);
		Short depositAuditFlag = borrowSetting.getDepositAuditFlag();

		if (depositAuditFlag==0){
			bookDamaged.setJudge(-1);
		}else{
			bookDamaged.setJudge(0);
		}
		bookDamaged.setBookStatus(6);
		bookDamaged.setLastStatus(0);
		bookDamaged.setRecorder(CurrentUser.getUserName());
		bookDamaged.setRecorderCode(CurrentUser.getUserCode());
		bookDamaged.setRecordTime(new Date());
		bookDamaged.save();

	}

}