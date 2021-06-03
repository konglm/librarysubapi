package com.jfnice._gen.#(lowercaseModelName);

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.jfinal.aop.Inject;
import com.jfnice.admin.setting.SettingService;
import com.jfnice.ext.CondPara;
import com.jfnice.model.#(modelName);

public class #(modelName)Logic {
	
	@Inject
	private #(modelName)Service #(firstCharLowerModelName)Service;
	@Inject
	private SettingService settingService;
	
	public SXSSFWorkbook createExcel(CondPara condPara) {
		List<#(modelName)> #(firstCharLowerModelName)List = #(firstCharLowerModelName)Service.getTreeList(condPara.getFields(), true);
		Map<String, String> keyTitleMap = settingService.getDefaultKeyTitleMap(condPara.getAccess());
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("sheet1");
		SXSSFRow row = sheet.createRow(0);
		SXSSFCell cell;
		
		CellStyle cellStyle = wb.createCellStyle();
		Font cellFont = wb.createFont();
		cellFont.setFontName("宋体");
		cellFont.setFontHeightInPoints((short)10);
		cellStyle.setFont(cellFont);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		List<String> exportableKeyList = condPara.getExportableKeyList();
		for ( int i = 0, size = exportableKeyList.size(); i < size; i++ ) {
			sheet.setDefaultColumnStyle(i, cellStyle);
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyTitleMap.get(exportableKeyList.get(i)));
		}
		
		buildExcelByLevel(sheet, new int[]{1}, 0, #(firstCharLowerModelName)List, exportableKeyList);
		return wb;
	}
	private void buildExcelByLevel(SXSSFSheet sheet, int[] rowIndex, int level, List<#(modelName)> #(firstCharLowerModelName)List, List<String> exportableKeyList) {
		SXSSFRow row;
		SXSSFCell cell;
		String key;
		String leftPad = "";
		#(modelName) #(firstCharLowerModelName);
		
		for ( int i = 0, len = #(firstCharLowerModelName)List.size(); i < len; i++ ) {
			if ( i == 0 ) {
				leftPad = level > 0 ? String.format("%1$" + (level*6 - 3) + "s|-- ", "") : "";
				level++;
			}

			#(firstCharLowerModelName) = #(firstCharLowerModelName)List.get(i);
			row = sheet.createRow(rowIndex[0]);
			for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
				cell = row.createCell(j);
				cell.setCellStyle(sheet.getColumnStyle(j));
				key = exportableKeyList.get(j);
				switch ( key ) {
					case "sort":
						cell.setCellValue(#(firstCharLowerModelName).getSort());
						break;
					case "name":
						cell.setCellValue(leftPad + #(firstCharLowerModelName).getName());
						break;
					case "status":
						cell.setCellValue(#(firstCharLowerModelName).getStatus() == 1 ? "正常" : "禁用");
						break;
					default:
						cell.setCellValue(#(firstCharLowerModelName).getStr(key));
				}
			}
			rowIndex[0] += 1;
			
			List<#(modelName)> children = #(firstCharLowerModelName).get("children");
			if ( children != null && !children.isEmpty() ) {
				buildExcelByLevel(sheet, rowIndex, level, children, exportableKeyList);
			}
		}
	}
	
}
