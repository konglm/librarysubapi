package com.school.library.borrowbook;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.jfinal.aop.Inject;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.ext.CondPara;
import com.jfinal.plugin.activerecord.Record;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.*;
import com.school.library.book.BookService;
import com.school.library.bookbarcode.BookBarCodeLogic;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.constants.SysConstants;
import com.school.library.kit.CommonKit;
import com.school.library.userinfo.UserInfoLogic;
import com.school.library.userinfo.UserInfoService;
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

public class BorrowBookLogic {

	@Inject
	private BorrowBookService service;

	@Inject
	private BookService bookService;
	@Inject
	private BorrowSettingLogic settingLogic;

	@Inject
	private UserInfoLogic userInfoLogic;

	@Inject
	private BookBarCodeLogic bookBarCodeLogic;



	public Page<BorrowBook> queryPage(CondPara condPara) {
		buildCondPara(condPara);
		return service.queryPage(condPara);
	}

	private void buildCondPara(CondPara condPara) {
		condPara.addCondition("name LIKE ", StrKit.isBlank(condPara.getAs("keyword")) ? null : "%" + condPara.getStr("keyword") + "%");
	}

	public List<BorrowBook> queryList() {
		return service.queryList("*");
	}

	/**
	 * 查询用户借书信息
	 * @param stuCode
	 * @return
	 */
	public Record getUserInfoById(Long stuCode,Long userCode){
		Kv kv = new Kv();
		if (stuCode != 0L){
			kv.set("stu_code", stuCode);
		}else if(userCode != 0L){
			kv.set("user_code",userCode);
		}
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.getUserInfoById", kv);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 查询是否借出图书
	 */
	public boolean isBorrow(String barCode ,String schoolCode){
		Kv kv = Kv.by("school_code", schoolCode)
				.set("bar_code",barCode);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.checkBorrow", kv);
		Record record = Db.findFirst(sqlPara);
		if (record != null){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 借书
	 */
	public void borrowBook(Long stuCode,Long userCode,String  barCodeList,int borrowed,String schoolCode){
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(schoolCode);
		Record userInfo = getUserInfoById(stuCode,userCode);
		Integer minDeposit = borrowSetting.getMinDeposit();
		int userDeposit = Integer.parseInt(userInfo.getStr("deposit"));
		if(userDeposit<minDeposit){
			throw new ErrorMsg("押金不足!");
		}
		int maxNum = borrowSetting.getMaxBorrowCount();
		List<String> barList = new ArrayList<>(Arrays.asList(barCodeList.split(",")));
		if(barList.size()+borrowed>maxNum){
			throw new ErrorMsg("超出最大借阅图书数量!");
		}
		List<Record> err = checkBookStatus(barList, schoolCode,1);
		if(!err.isEmpty()){
			List<String> bookNames =new ArrayList<>();
			for(Record record:err){
				String book_name = record.getStr("book_name");
				bookNames.add(book_name);
			}
			throw new ErrorMsg("部分图书不能被借出,图书为:"+String.join(",",bookNames));
		}

		List<BorrowBook> borrowList = new ArrayList<>();
		for (String barCode:barList){
			BookBarCode barcode = bookBarCodeLogic.findByBarcode(barCode, schoolCode);
			Book bookinfo = bookService.queryByBarCode(schoolCode, barCode);

			if (barcode.getStatus() != 1){
				if(barcode.getStatus() == 0){
					throw new ErrorMsg("该图书未登记:"+bookinfo.getBookName());
				}else {
					throw new ErrorMsg("该图书已破损或损毁:"+bookinfo.getBookName());
				}
			}
			BorrowBook borrowBook = new BorrowBook();
			borrowBook.setAuthor(bookinfo.getAuthor());
			borrowBook.setDel(false);
			borrowBook.setCreateUserName(CurrentUser.getUserName());
			borrowBook.setCreateUserCode(CurrentUser.getUserCode());
			borrowBook.setCreateTime(new Date());
			borrowBook.setSchoolCode(schoolCode);
			borrowBook.setSno(userInfo.getStr("sno"));
			if(userInfo.getStr("user_type").equals("stu")){
				borrowBook.setBorrower(userInfo.getStr("stu_name"));
				borrowBook.setGrdCode(userInfo.getStr("grd_code"));
				borrowBook.setGrdName(userInfo.getStr("grd_name"));
				borrowBook.setClsCode(userInfo.getStr("cls_code"));
				borrowBook.setClsName(userInfo.getStr("cls_name"));
				borrowBook.setStuCode(userInfo.getStr("stu_code"));
			}else{
			    borrowBook.setDptCode(userInfo.getStr("dpt_code"));
				borrowBook.setDptName(userInfo.getStr("dpt_name"));
				borrowBook.setUserCode(userInfo.getStr("user_code"));
				borrowBook.setBorrower(userInfo.getStr("user_name"));
			}
			borrowBook.setCatalogNo(bookinfo.getCatalogNo());
			borrowBook.setCatalogName(bookinfo.getCatalogName());
			borrowBook.setCheckNo(bookinfo.getCheckNo());
			borrowBook.setPrice(bookinfo.getInt("price"));
			borrowBook.setBookName(bookinfo.getBookName());
			borrowBook.setPublisher(bookinfo.getPublisher());
			borrowBook.setPublishDate(bookinfo.getPublishDate());
			borrowBook.setBarCode(barCode);
			borrowBook.setBorrowTime(new Date());
			borrowBook.setReturnStatus(0);
			borrowBook.setBookImgUrl(bookinfo.getBookImgUrl());
			borrowBook.setBookId(bookinfo.getId());
			borrowList.add(borrowBook);
		}
		service.batchSave(borrowList);
	}

	/**
	 * 图书是否已借出或归还
	 */
	public List<Record> checkBookStatus(List<String> barList,String schoolCode,int status){
		Kv kv = Kv.by("school_code", schoolCode)
				.set("barCodes",barList)
				.set("status",status);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.checkBookStatus", kv);
		return Db.find(sqlPara);
	}

	/**
	 * 用户未归还的书籍
	 * @param userCode
	 * @param stuCode
	 * @return
	 */
	public List<BorrowBook> queryUnReturnByUser(String userCode, String stuCode){
		if(Strings.isNullOrEmpty(userCode) && Strings.isNullOrEmpty(stuCode)){
			return new ArrayList<>();
		}
		Kv kv = Kv.by("stu_code", stuCode);
		kv.set("user_code", userCode);
		kv.set("school_code", CurrentUser.getSchoolCode());
		kv.set("return_status", ReturnStatusEnum.UN_RETURN.getStatus());
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.queryUnReturnByUser", kv);
		return BorrowBook.dao.find(sqlPara);
	}

	/**
	 * 分页查询用户借阅历史
	 * @param userCode
	 * @param stuCode
	 * @return
	 */
	public Page<BorrowBook> pageReturnByUser(String userCode, String stuCode, int pageNumber, int pageSize){
		Kv kv = Kv.by("stu_code", stuCode);
		kv.set("user_code", userCode);
		kv.set("school_code", CurrentUser.getSchoolCode());
		kv.set("return_status", ReturnStatusEnum.UN_RETURN.getStatus());
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.queryReturnByUser", kv);
		return BorrowBook.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 统计某段时间借阅总次数
	 * @param begintime
	 * @param endtime
	 * @return
	 */
	public int statisticsTotal(String begintime, String endtime){
		Kv kv = Kv.by("school_code", CurrentUser.getSchoolCode());
		kv.set("begintime", begintime);
		kv.set("endtime", endtime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsTotal", kv);
		Record r = Db.findFirst(sqlPara);
		return r == null ? 0 : r.getInt("borrow_count");
	}

	/**
	 * 分页统计按分类号借阅的次数
	 * @param begintime
	 * @param endtime
	 * @return
	 */
	public Page<Record> statisticsByCatalogNo(String begintime, String endtime, int pageNumber, int pageSize){
		Kv kv = Kv.by("school_code", CurrentUser.getSchoolCode());
		kv.set("begintime", begintime);
		kv.set("endtime", endtime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsByCatalogNo", kv);
		Page<Record> page = Db.paginate(pageNumber, pageSize, sqlPara);
		return page;
	}

	/**
	 * 分页查询借阅超时记录
	 * @return
	 */
	public Page<BorrowBook> pageOver(int pageNumber, int pageSize){
		//查询借出设置
		BorrowSetting borrowSetting = this.settingLogic.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		int sysBorrowDays = borrowSetting.getBorrowDays();
		Kv kv = Kv.by("school_code", CurrentUser.getSchoolCode());
		kv.set("limit_time", DateKit.toStr(CommonKit.addDays(new Date(), (-sysBorrowDays)),"yyyy-MM-dd"));
		kv.set("return_status", ReturnStatusEnum.UN_RETURN.getStatus());
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.queryOver", kv);
		Page<BorrowBook> page = BorrowBook.dao.paginate(pageNumber, pageSize, sqlPara);
		page.getList().forEach(b -> {
			long borrowDays = CommonKit.differDays(b.getBorrowTime(), new Date());
			long beyondDays = 0;
			if(borrowDays > sysBorrowDays){
				beyondDays = borrowDays - sysBorrowDays;
			}
			b.put("over_days", beyondDays);
			b.put("borrow_time", DateKit.toStr(b.getBorrowTime(), "yyyy-MM-dd"));
			b.put("user_type_txt", StrKit.notBlank(b.getStuCode()) ?
					SysConstants.STUDENT_TXT_USER_TYPE : SysConstants.TEACHER_TXT_USER_TYPE);
		});
		return page;
	}

	/**
	 * 查询超期天数
	 * @param borrowTime
	 * @return
	 */
	public Integer overDays(Date borrowTime){
		BorrowSetting borrowSetting = this.settingLogic.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		int sysBorrowDays = borrowSetting.getBorrowDays();
		long borrowDays = CommonKit.differDays(borrowTime, new Date());
		Integer beyondDays = null;
		if(borrowDays > sysBorrowDays){
			beyondDays = (int)borrowDays - sysBorrowDays;
		}
		return beyondDays;
	}

	/**
	 * 分页统计按书名借阅的次数
	 * @param begintime
	 * @param endtime
	 * @return
	 */
	public Page<Record> statisticsByBook(String begintime, String endtime, int pageNumber, int pageSize){
		Kv kv = Kv.by("school_code", CurrentUser.getSchoolCode());
		kv.set("begintime", begintime);
		kv.set("endtime", endtime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsByBook", kv);
		Page<Record> page = Db.paginate(pageNumber, pageSize, sqlPara);
		return page;
	}

	public Page<Record> statisticsBorrowZeroByCatalogNo(String begintime, String endtime, int pageNumber, int pageSize){
		Kv kv = Kv.by("school_code", CurrentUser.getSchoolCode());
		kv.set("begintime", begintime);
		kv.set("endtime", endtime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsBorrowZeroByCatalogNo", kv);
		Page<Record> page = Db.paginate(pageNumber, pageSize, sqlPara);
		return page;
	}

	/**
	 * 通过bar_code查询还书信息
	 * @param schoolCode
	 * @param barCode
	 * @return
	 * @throws ParseException
	 */
	public Record payBookByBarCode(String schoolCode,String barCode) throws ParseException {
		Kv kv = Kv.by("school_code", schoolCode)
				.set("bar_code",barCode);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.PayBookByBarCode", kv);
		Record record = Db.findFirst(sqlPara);
		if(record == null){
			throw new ErrorMsg("该图书未被借出!");
		}
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(schoolCode);
		Integer borrowDays = borrowSetting.getBorrowDays();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		if (money > 10000) {
			money = 10000;
		}
		String price = CommonKit.formatMoney(Integer.parseInt(record.getStr("price")));
		record.set("price", price);
		String moneys =CommonKit.formatMoney(money);
		record.set("cost", moneys);
		return record;
	}

	/**
	 * 用户未归还书籍列表
	 * @param schoolCode
	 * @param userType
	 * @param userCode
	 * @return
	 * @throws ParseException
	 */
	public List<Record> paybookList(String schoolCode,String userType,String userCode) throws ParseException {

		Kv kv = Kv.by("school_code", schoolCode)
				.set("user_type",userType)
				.set("user_code",userCode);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.paybookList", kv);
		List<Record> payList = Db.find(sqlPara);
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(schoolCode);
		Integer borrowDays = borrowSetting.getBorrowDays();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!payList.isEmpty()){
			for (Record record:payList) {
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
				if (money > 10000) {
					money = 10000;
				}


				String price = CommonKit.formatMoney(Integer.parseInt(record.getStr("price")));
				record.set("price", price);
                String moneys =CommonKit.formatMoney(money);
				record.set("cost", moneys);

				}
			}
		return payList;
	}

	/**
	 * 计算费用
	 */
	public Integer cost(String schoolCode,Date borrow_time){
		BorrowSetting borrowSetting = settingLogic.queryIfNotNewBySchool(schoolCode);
		Integer borrowDays = borrowSetting.getBorrowDays();
		int borrowDay = (int) ((new Date().getTime() - borrow_time.getTime()) / (1000 * 3600 * 24));
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
		} else {
			money = borrowDay * borrowSetting.getUnitCost();
		}
		if (money > 10000) {
			money = 10000;
		}
		return money;
	}

	/**
	 * 还书
	 */
	public void payBook(String userType,String userCode,String  barCodeList,String schoolCode){
		String[] barCodes = barCodeList.trim().replace("，", ",").split(",");
		Kv kv = Kv.by("school_code", schoolCode)
				.set("barCodes",barCodes)
				.set("user_type",userType)
				.set("user_code",userCode);
		List<String> barList = Arrays.asList(barCodes);
		List<Record> err = checkBookStatus(barList, schoolCode,0);
		if(barList.size()>err.size()){
			List<String> bookCodes =new ArrayList<>();
			for(String bar:barList){
				boolean isPay = true;
				for(Record record:err) {
					String bar_code = record.getStr("bar_code");
					if(bar.equals(bar_code)){
						isPay = false;
					}
				}
				if (isPay){
					bookCodes.add(bar);
				}
			}
			throw new ErrorMsg("部分图书非借出状态,不能归还,非借出状态的图书编号为:"+String.join(",",bookCodes));
		}
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.payBook", kv);
		List<BorrowBook> borrowBooks = BorrowBook.dao.find(sqlPara);
		int totalcost = 0;
		for(BorrowBook borrow:borrowBooks){
			borrow.setUpdateTime(new Date());
			borrow.setUpdateUserCode(CurrentUser.getUserCode());
			borrow.setReturnTime(new Date());
			borrow.setReturnStatus(1);
			borrow.setBookStatus(1);
			borrow.setDeductions(cost(schoolCode,borrow.getBorrowTime()));
			totalcost = totalcost +borrow.getDeductions();
		}
		UserInfo userInfo = userInfoLogic.findByUserCode(schoolCode, userCode, userType);
		userInfo.setDeposit(userInfo.getDeposit()-totalcost);
		userInfo.update();
		service.batchUpdate(borrowBooks);
	}

	/**
	 * 押金扣除记录
	 * @param keywords
	 * @param startTime
	 * @param endTime
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> depositList(String keywords, String startTime, String endTime, int pageNumber, int pageSize) {
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",startTime)
				.set("end_time",endTime)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.depositList", kv);
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

	public SXSSFWorkbook createExcelDepositList(String keywords, String startDate, String endDate) {

		JSONObject json = new JSONObject();
		String tableTitle = "押金扣除记录";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("身份", "user_type");
		headMap.put("部门", "dpt_name");
		headMap.put("年级", "grd_name");
		headMap.put("班级", "cls_name");
		headMap.put("姓名", "borrower");
		headMap.put("图书编号", "bar_code");
		headMap.put("书名", "book_name");
		headMap.put("超期天数", "over_days");
		headMap.put("归还情况", "return_status");
		headMap.put("押金扣除/元", "deductions");
		headMap.put("扣除时间", "update_time");

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
		Page<Record> page= this.depositList(keywords, startDate, endDate, pageNumber, pageSize);
		for (Record record:page.getList()){
			if ("".equals(record.getStr("stu_code"))) {
				record.set("user_type", "教师");
			} else {
				record.set("user_type", "学生");
			}
			if ("0".equals(record.getStr("return_status"))) {
				record.set("return_status", "未归还");
			} else if ("1".equals(record.getStr("return_status"))) {
				record.set("return_status", "已归还");
			} else if ("2".equals(record.getStr("return_status"))) {
				record.set("return_status", "破损");
			} else if ("3".equals(record.getStr("return_status"))) {
				record.set("return_status", "损毁");
			} else {
				record.set("return_status", "丢失");
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
							case "over_days":
								if(r.get(key) == null) {
									cell.setCellValue(0);
								} else {
									cell.setCellValue(r.getStr(key));
								}
								break;
							case "deductions":
								if(r.get(key) == null) {
									cell.setCellValue(0);
								} else {
									cell.setCellValue((double) r.getInt(key) / 100);
								}
								break;
							case "update_time":
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
			page = this.depositList(keywords, startDate, endDate, pageNumber, pageSize);
		}
		return wb;
	}

	public String getTotalDepositAmount(String keywords, String startTime, String endTime) {
		Kv kv = Kv.by("keywords", keywords)
				.set("start_time",startTime)
				.set("end_time",endTime)
				.set("school_code", CurrentUser.getSchoolCode());
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.getTotalDepositAmount", kv);
		Record record = Db.findFirst(sqlPara);

		return record.getStr("total_amount");
	}
}