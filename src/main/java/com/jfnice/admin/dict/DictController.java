package com.jfnice.admin.dict;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ExcelExport;
import com.jfnice.interceptor.TxPost;
import com.jfnice.kit.UrlKit;
import com.jfnice.model.Dict;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Map;

/**
 * 字典控制器
 */
@Before(DictValidator.class)
public class DictController extends JFniceBaseController {

    @Inject
    private DictLogic logic;
    @Inject
    private DictService dictService;

    public void index(@Para("tag") String tag){
        ok(DictKit.getList(tag));
    }

    /**
     * 列表
     */
    public void list() {
        CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
        Page<Dict> page = logic.queryPageOrList(condPara);
        renderJson(page);
    }

    /**
     * 导出excel
     */
    public void exportExcel() {
        CondPara condPara = JsonKit.parse(UrlKit.decodeUrl(getPara("paras")), CondPara.class);
        SXSSFWorkbook wb = logic.createExcel(condPara);
        render(new ExcelExport(wb, "字典表"));
    }

    @Before(TxPost.class)
    public void importExcel() {
        if (!isPost()) {
            render("import.html");
        } else {
            importExcelPost();
        }
    }

    private void importExcelPost() {
        logic.importExcel(getPara("fileUrl"));
        ok("导入成功！");
    }

    @Before(TxPost.class)
    public void add() {
        if (!isPost()) {
            render("add.html");
        } else {
            addPost();
        }
    }

    private void addPost() {
        Dict dict = getModel(Dict.class, "", true);
        dictService.save(dict);
        ok("添加成功！");
    }

    @Before(TxPost.class)
    public void edit() {
        if (!isPost()) {
            long dictId = getParaToLong("id", 0L);
            Dict dict = dictService.queryById(dictId);
            setAttr("dict", dict);
            render("edit.html");
        } else {
            editPost();
        }
    }

    private void editPost() {
        Dict dict = getModel(Dict.class, "", true);
        dictService.update(dict);
        ok("保存成功！");
    }

    @Before(Tx.class)
    public void delete() {
        long dictId = getParaToLong("id", 0L);
        dictService.deleteById(dictId, true);
        ok("删除成功！");
    }

    @Before(Tx.class)
    public void batchDelete() {
        Long[] dictIds = getParaValuesToLong("ids[]");
        dictService.batchDelete(dictIds, true);
        ok("删除成功！");
    }

    @Before(Tx.class)
    public void sort() {
        Map<Long, Long> map = JSON.parseObject(getPara("sorts"), new TypeReference<Map<Long, Long>>() {
        });
        dictService.sort(map);
        ok("排序成功！");
    }

}