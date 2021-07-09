package com.school.library.statistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.ExcelExport;
import com.school.library.constants.DictConstants;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * 图书统计与分析
 * @Description
 * @Author jsy
 * @Date 2020/3/27
 * @Version V1.0
 **/

public class StatisticsController extends JFniceBaseController {

    @Inject
    private StatisticsLogic statisticsLogic;

    /**
     * 统计书本数量
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsBooks(){
        ok(this.statisticsLogic.statisticsBooks());
    }

    /**
     * 统计按分类号借阅次数
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsBorrowCountByCatalogNo(){
        String begintime = getPara("begintime");
        String endtime = getPara("endtime");
        Integer pageNumber = getParaToInt("page_number", 1);
        Integer pageSize = getParaToInt("page_size", 15);
        ok(this.statisticsLogic.statisticsBorrowCountByCatalogNo(begintime, endtime, pageNumber, pageSize));
    }

    /**
     * 分页查询借阅超时记录
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void pageOver(){
        Integer pageNumber = getParaToInt("page_number", 1);
        Integer pageSize = getParaToInt("page_size", 15);
        ok(this.statisticsLogic.pageOver(pageNumber, pageSize));
    }

    /**
     * 下载借阅超时记录
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void excelOver(){
        SXSSFWorkbook wb = this.statisticsLogic.creteExcelOver();
        render(new ExcelExport(wb, "借阅超时统计"));

    }

    /**
     * 统计按书名借阅次数
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsBorrowCountByBook(){
        String begintime = getPara("begintime");
        String endtime = getPara("endtime");
        Integer pageNumber = getParaToInt("page_number", 1);
        Integer pageSize = getParaToInt("page_size", 15);
        ok(this.statisticsLogic.statisticsBorrowCountByBook(begintime, endtime, pageNumber, pageSize));
    }

    /**
     * 查询检索词统计周期字典
     */
    public void dict(){
        JSONArray cycleArray = JSON.parseArray(DictKit.toJsonArray(DictConstants.SEARCH_STATISTICS_CYCLE_TAG));
        JSONObject result = new JSONObject();
        result.put("cycle_array", cycleArray);
        ok(result);
    }

    /**
     * 统计检索
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsSearch(){
        String cycle = getPara("cycle");
        Integer pageNumber = getParaToInt("page_number", 1);
        Integer pageSize = getParaToInt("page_size", 100);
        ok(this.statisticsLogic.pageStatisticKeyWord(cycle, pageNumber, pageSize));
    }

    /**
     * 统计总数
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsTotal(){
        ok(this.statisticsLogic.statisticsTotal());
    }

    /**
     * 统计入库
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsStorage(){
        ok(this.statisticsLogic.statisticsStorage());
    }

    /**
     * 统计借书还书
     */
    @JsyPermissions(OpCodeEnum.INDEX)
    public void statisticsBorrowReturn(){
        ok(this.statisticsLogic.statisticsBorrowReturn());
    }

}
