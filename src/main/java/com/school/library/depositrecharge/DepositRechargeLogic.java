package com.school.library.depositrecharge;

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
import com.jfnice.model.BookDamaged;
import com.jfnice.model.DepositRecharge;
import com.school.library.kit.CommonKit;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.*;

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

	public SXSSFWorkbook createExcelDepositList(String keywords, String startDate, String endDate) {

		JSONObject json = new JSONObject();
		String tableTitle = "充值押金记录";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("姓名", "user_name");
		headMap.put("卡号", "card_no");
		headMap.put("身份", "user_type");
		headMap.put("部门", "dpt_name");
		headMap.put("年级", "grd_name");
		headMap.put("班级", "cls_name");
		headMap.put("充值金额/元", "recharge_amount");
		headMap.put("充值时间", "recharge_time");
		headMap.put("操作人", "create_user_name");

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
							case "recharge_amount":
								if(r.get(key) == null) {
									cell.setCellValue(0);
								} else {
									cell.setCellValue((double) r.getInt(key) / 100);
								}
								break;
							case "recharge_time":
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