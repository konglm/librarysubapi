package com.school.library.statistics;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.jfinal.aop.Inject;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.BorrowBook;
import com.school.api.map.ClsMap;
import com.school.api.map.GrdMap;
import com.school.library.bookbarcode.BookBarCodeService;
import com.school.library.bookbarcode.BookBarCodeStatusEnum;
import com.school.library.borrowbook.BorrowBookLogic;
import com.school.library.constants.SysConstants;
import com.school.library.kit.CommonKit;
import com.school.library.search.SearchService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description
 * @Author jsy
 * @Date 2020/3/27
 * @Version V1.0
 **/

public class StatisticsLogic {

    @Inject
    private BookBarCodeService bookBarCodeService;
    @Inject
    private BorrowBookLogic borrowBookLogic;
    @Inject
    private SearchService searchService;

    /**
     * 统计书本数量
     * @return
     */
    public JSONObject statisticsBooks(){
        BigDecimal base = new BigDecimal("100");
        List<Record> list = this.bookBarCodeService.statisticsByCatalogNo(CurrentUser.getSchoolCode(),
                BookBarCodeStatusEnum.STORAGE.getStatus());
        Record totalRecord = new Record();
        final int[] total_count = {0};
        list.stream().forEach(r -> {
            total_count[0] = total_count[0] + r.getInt("book_count");
        });
        list.stream().forEach(r -> {
            BigDecimal rate = BigDecimal.valueOf(r.getInt("book_count")).multiply(base).divide(BigDecimal.valueOf(total_count[0]),
                    2, BigDecimal.ROUND_HALF_UP);
            r.set("rate", String.valueOf(rate) + "%");
        });
        if(CollectionUtils.isNotEmpty(list)){
            list.add(0, totalRecord);
        }
        totalRecord.set("book_count", total_count[0]);
        totalRecord.set("rate", String.valueOf(base) + '%');
        JSONObject data = new JSONObject();
        data.put("list", list);
        return data;
    }

    /**
     * 统计按分类号借阅次数
     * @param begintime
     * @param endtime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public JSONObject statisticsBorrowCountByCatalogNo(String begintime, String endtime, int pageNumber, int pageSize){
        BigDecimal base = new BigDecimal("100");
        int totalCount = this.borrowBookLogic.statisticsTotal(begintime, endtime);
        Page<Record> page = this.borrowBookLogic.statisticsByCatalogNo(begintime, endtime, pageNumber, pageSize);
        page.getList().forEach(r -> {
            r.set("begintime", begintime);
            r.set("endtime", endtime);
            BigDecimal rate = BigDecimal.valueOf(r.getInt("borrow_count")).multiply(base).divide(BigDecimal.valueOf(totalCount),
                    2, BigDecimal.ROUND_HALF_UP);
            r.set("rate", String.valueOf(rate) + "%");
        });
        JSONObject data = new JSONObject();
        data.put("total_count", totalCount);
        data.put("total_page", page.getTotalPage());
        data.put("total_row", page.getTotalRow());
        data.put("page_number", page.getPageNumber());
        data.put("page_size", page.getPageSize());
        data.put("list", page.getList());
        return data;
    }

    /**
     * 分页查询超时借阅记录
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<BorrowBook> pageOver(int pageNumber, int pageSize){
        Page<BorrowBook> page = this.borrowBookLogic.pageOver(pageNumber, pageSize);
        return page;
    }

    /**
     * 生成超时借阅记录Excel
     * @return
     */
    public SXSSFWorkbook creteExcelOver() {

        JSONObject json = new JSONObject();
        String tableTitle = "借阅超时统计";
        Map<String, String> headMap = new LinkedHashMap<>();
        headMap.put("编号", "bar_code");
        headMap.put("书名", "book_name");
        headMap.put("著者", "author");
        headMap.put("借阅日期", "borrow_time");
        headMap.put("超时天数", "over_days");
        headMap.put("借阅人", "borrower");
        headMap.put("身份", "user_type_txt");
        headMap.put("部门", "dpt_name");
        headMap.put("年级", "grd_name");
        headMap.put("班级", "cls_name");

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

        int pageNumber = 1;
        int pageSize = 500;
        Page<BorrowBook> page= this.pageOver(pageNumber, pageSize);
        int totalRow = page.getTotalRow();
        int seqIndex = 1;
        while(page.getTotalPage() >= pageNumber){
            List<BorrowBook> list = page.getList();
            if(null!= list && !list.isEmpty()){
                for(int i = 0, len = list.size(); i < len; i++){
                    BorrowBook r = list.get(i);
                    row = sheet.createRow(rowIndex++);
                    for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
                        cell = row.createCell(j);
                        cell.setCellStyle(cellStyle);
                        key = exportableKeyList.get(j);
                        switch ( key ) {
                            case "seq":
                                cell.setCellValue(seqIndex++);
                                break;
                            default:
                                cell.setCellValue(r.getStr(key));
                        }
                    }

                }
            }
            pageNumber = pageNumber + 1;
            page = this.pageOver(pageNumber, pageSize);
        }
        return wb;
    }

    /**
     * 统计按书名借阅次数
     * @param begintime
     * @param endtime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> statisticsBorrowCountByBook(String begintime, String endtime, int pageNumber, int pageSize){
        Page<Record> page = this.borrowBookLogic.statisticsByBook(begintime, endtime, pageNumber, pageSize);
        page.getList().forEach(r -> {
            r.set("begintime", begintime);
            r.set("endtime", endtime);
            r.set("publish_date", DateKit.toStr(r.getDate("publish_date"), "yyyy-MM"));
        });
        return page;
    }

    /**
     * 统计检索次数
     * @param cycle 统计周期
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> pageStatisticKeyWord(String cycle, int pageNumber, int pageSize){
        String begintime = DateKit.toStr(new Date(), "yyyy-MM-dd");
        String endtime = DateKit.toStr(new Date(), "yyyy-MM-dd");
        if(Objects.equals(StatisticsSearchCycleEnum.LAST_MONTH.getCycle(), cycle)){ //近一个月
            endtime = DateKit.toStr(new Date(), "yyyy-MM-dd");
            begintime = DateKit.toStr(CommonKit.addMonths(new Date(), -1), "yyyy-MM-dd");
        }else if(Objects.equals(StatisticsSearchCycleEnum.ONE_YEAR.getCycle(), cycle)){ //近一年
            endtime = DateKit.toStr(new Date(), "yyyy-MM-dd");
            begintime = DateKit.toStr(CommonKit.addYears(new Date(), -1), "yyyy-MM-dd");
        }else if(Objects.equals(StatisticsSearchCycleEnum.TWO_YEAR.getCycle(), cycle)){// 近两年
            endtime = DateKit.toStr(new Date(), "yyyy-MM-dd");
            begintime = DateKit.toStr(CommonKit.addYears(new Date(), -2), "yyyy-MM-dd");
        }else if(Objects.equals(StatisticsSearchCycleEnum.ALL.getCycle(), cycle)){// 全部
            begintime = "";
            endtime = "";
        }
        Page<Record> page = this.searchService.pageStatisticKeyWord(CurrentUser.getSchoolCode(), begintime, endtime,
                pageNumber, pageSize);
        return page;
    }

}
