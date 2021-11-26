package com.school.library.statistics;

import com.alibaba.fastjson.JSONArray;
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
import com.school.library.bookstorage.BookStorageService;
import com.school.library.bookstorageitembarcode.BookStorageItemBarCodeService;
import com.school.library.borrowbook.BorrowBookLogic;
import com.school.library.borrowbook.BorrowBookService;
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
import java.text.SimpleDateFormat;
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
    @Inject
    private BookStorageService storageService;
    @Inject
    private BorrowBookService borrowBookService;
    @Inject
    private BookStorageItemBarCodeService bookStorageItemBarCodeService;

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
     * 统计未借阅类型
     * @param begintime
     * @param endtime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> statisticsBorrowZeroByCatalogNo(String begintime, String endtime, int pageNumber, int pageSize){
        Page<Record> page = this.borrowBookLogic.statisticsBorrowZeroByCatalogNo(begintime, endtime, pageNumber, pageSize);
        page.getList().forEach(r -> {
            r.set("begintime", begintime);
            r.set("endtime", endtime);
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

    /**
     * 统计总数
     * @return
     */
    public JSONObject statisticsTotal(){
        Record recordTotalCnt = this.bookBarCodeService.statisticsTotalCnt(CurrentUser.getSchoolCode());
        Record recordTotalAmount = this.bookBarCodeService.statisticsTotalAmount(CurrentUser.getSchoolCode());
        Record recordTotalIn = this.bookBarCodeService.statisticsTotalIn(CurrentUser.getSchoolCode());
        Record recordTotalOut = this.bookBarCodeService.statisticsTotalOut(CurrentUser.getSchoolCode());
        Record recordTotalRepair = this.bookBarCodeService.statisticsTotalRepair(CurrentUser.getSchoolCode());
        JSONObject data = new JSONObject();
        int totalCnt = recordTotalCnt.getInt("total_cnt");
        data.put("total_cnt", totalCnt);
        data.put("total_amount", recordTotalAmount.getStr("total_amount"));

        int totalIn = recordTotalIn.getInt("total_cnt");
        data.put("total_in", totalIn);
        data.put("total_in_ratio", CommonKit.getRatio(totalIn, totalCnt));

        int totalOut = recordTotalOut.getInt("total_cnt");
        data.put("total_out", totalOut);
        data.put("total_out_ratio", CommonKit.getRatio(totalOut, totalCnt));

        int totalRepair = recordTotalRepair.getInt("total_cnt");
        data.put("total_repair", totalRepair);
        data.put("total_repair_ratio", CommonKit.getRatio(totalRepair, totalCnt));

        return data;
    }

    /**
     * 统计库存
     * @return
     */
    public JSONObject statisticsStorage(){
        Record recordTotalStorage = this.bookStorageItemBarCodeService.statisticsTotalStorage(CurrentUser.getSchoolCode());
        Record recordTotalDamage = this.bookBarCodeService.statisticsTotalDamage(CurrentUser.getSchoolCode());
        Record recordTotalLose = this.bookBarCodeService.statisticsTotalLose(CurrentUser.getSchoolCode());
        Record recordTotalWriteOff = this.bookBarCodeService.statisticsTotalWriteOff(CurrentUser.getSchoolCode());
        JSONObject data = new JSONObject();
        data.put("total_storage_cnt", recordTotalStorage.getStr("total_storage_cnt"));
        data.put("total_damage_cnt", recordTotalDamage.getStr("total_damage_cnt"));
        data.put("total_lose_cnt", recordTotalLose.getStr("total_lose_cnt"));
        data.put("total_write_off_cnt", recordTotalWriteOff.getStr("total_write_off_cnt"));
        data.put("total_storage_amount", recordTotalStorage.getStr("total_storage_amount"));
        data.put("total_damage_amount", recordTotalDamage.getStr("total_damage_amount"));
        data.put("total_lose_amount", recordTotalLose.getStr("total_lose_amount"));
        data.put("total_write_off_amount", recordTotalWriteOff.getStr("total_write_off_amount"));
        return data;
    }

    /**
     * 统计借书还书
     * @return
     */
    public JSONObject statisticsBorrowReturn(){
        JSONArray monthList = new JSONArray();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");

        cal.add(Calendar.MONTH, 0); //当月
        int dateYear = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow = this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear,dateMonth))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear,dateMonth)));
        Record recordReturn = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear,dateMonth))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear,dateMonth)));
        JSONObject data = new JSONObject();
        data.put("month", dateMonth);
        data.put("borrow_cnt", recordBorrow.getStr("borrow_cnt"));
        data.put("return_cnt", recordReturn.getStr("return_cnt"));
        monthList.add(data);

        cal.add(Calendar.MONTH, -1); //上一月
        int dateYear1 = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth1 = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow1 = this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear1,dateMonth1))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear1,dateMonth1)));
        Record recordReturn1 = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear1,dateMonth1))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear1,dateMonth1)));
        JSONObject data1 = new JSONObject();
        data1.put("month", dateMonth1);
        data1.put("borrow_cnt", recordBorrow1.getStr("borrow_cnt"));
        data1.put("return_cnt", recordReturn1.getStr("return_cnt"));
        monthList.add(data1);

        cal.add(Calendar.MONTH, -1); //上两月
        int dateYear2 = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth2 = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow2 = this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear2,dateMonth2))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear2,dateMonth2)));
        Record recordReturn2 = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear2,dateMonth2))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear2,dateMonth2)));
        JSONObject data2 = new JSONObject();
        data2.put("month", dateMonth2);
        data2.put("borrow_cnt", recordBorrow2.getStr("borrow_cnt"));
        data2.put("return_cnt", recordReturn2.getStr("return_cnt"));
        monthList.add(data2);

        cal.add(Calendar.MONTH, -1); //上三月
        int dateYear3 = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth3 = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow3 = this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear3,dateMonth3))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear3,dateMonth3)));
        Record recordReturn3 = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear3,dateMonth3))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear3,dateMonth3)));
        JSONObject data3 = new JSONObject();
        data3.put("month", dateMonth3);
        data3.put("borrow_cnt", recordBorrow3.getStr("borrow_cnt"));
        data3.put("return_cnt", recordReturn3.getStr("return_cnt"));
        monthList.add(data3);

        cal.add(Calendar.MONTH, -1); //上四月
        int dateYear4 = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth4 = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow4 = this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear4,dateMonth4))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear4,dateMonth4)));
        Record recordReturn4 = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear4,dateMonth4))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear4,dateMonth4)));
        JSONObject data4 = new JSONObject();
        data4.put("month", dateMonth4);
        data4.put("borrow_cnt", recordBorrow4.getStr("borrow_cnt"));
        data4.put("return_cnt", recordReturn4.getStr("return_cnt"));
        monthList.add(data4);

        cal.add(Calendar.MONTH, -1); //上五月
        int dateYear5 = Integer.parseInt(sdfYear.format(cal.getTime()));
        int dateMonth5 = Integer.parseInt(sdfMonth.format(cal.getTime()));
        Record recordBorrow5= this.borrowBookService.statisticsBorrowCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear5,dateMonth5))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear5,dateMonth5)));
        Record recordReturn5 = this.borrowBookService.statisticsReturnCnt(CurrentUser.getSchoolCode()
                , CommonKit.dealStartTime(CommonKit.getBeginTime(dateYear5,dateMonth5))
                , CommonKit.dealEndTime(CommonKit.getEndTime(dateYear5,dateMonth5)));
        JSONObject data5 = new JSONObject();
        data5.put("month", dateMonth5);
        data5.put("borrow_cnt", recordBorrow5.getStr("borrow_cnt"));
        data5.put("return_cnt", recordReturn5.getStr("return_cnt"));
        monthList.add(data5);

        JSONObject dataResult = new JSONObject();
        dataResult.put("list", monthList);

        return dataResult;
    }

}
