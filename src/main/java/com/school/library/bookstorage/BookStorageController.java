package com.school.library.bookstorage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.ExcelExport;
import com.jfnice.model.BookDamaged;
import com.jfnice.model.BookStorageItem;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.BookStorage;
import com.school.library.constants.DictConstants;
import com.school.library.constants.SysConstants;
import com.school.library.kit.CommonKit;
import com.school.library.kit.JsyAddDelEdit;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.ParseException;

/**
 * 图书入库
 */
@Before({BookStorageValidator.class, DelEditValidator.class})
public class BookStorageController extends JFniceBaseController {

	@Inject
	private BookStorageLogic logic;
	@Inject
	private BookStorageService service;

	/**
	 * 查询字典
	 */
	public void dict(){
		JSONArray statusArray = JSON.parseArray(DictKit.toJsonArray(DictConstants.STORAGE_STATUS_TAG));
		JSONObject result = new JSONObject();
		result.put("status", statusArray);
		ok(result);
	}

	/**
	 * 首页分页查询
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<BookStorage> page = logic.queryIndexPage(condPara);
		ok(page);
	}

	/**
	 * 新建图书入库
	 */
	@JsyPermissions(OpCodeEnum.ADD)
	@Before(TxPost.class)
	public void add() {
		BookStorage s = this.logic.save();
		JSONObject data = new JSONObject();
		data.put("storage", s);
		ok("保存成功！", data);
	}

	/**
	 * 删除入库
	 * @param id
	 */
	@JsyPermissions(value = OpCodeEnum.DELETE, pass = true)
	@JsyAddDelEdit(requirePrimary = true)
	@Before(TxPost.class)
	public void delete(@Para(value = "id", defaultValue = "0") long id) {
		boolean reuslt = this.logic.logicDelete(id);
		if(reuslt){
			ok("删除成功！");
		}else{
			fail("删除失败");
		}

	}

	/**
	 * 结束入库（确认入库）
	 * @param id
	 */
	@JsyAddDelEdit(requirePrimary = true)
	@Before(TxPost.class)
	public void over(@Para(value = "id", defaultValue = "0") long id) {
		boolean result = this.logic.over(id);
		if(result){
			ok("确认入库成功！");
		}else{
			fail("确认入库失败");
		}

	}

	/**
	 * 入库明细添加、编辑
	 */
	@JsyAddDelEdit(requirePrimary = true, primaryParam = "book_storage_id")
	@Before(TxPost.class)
	public void saveItem(){
		BookStorageItem bookStorageItem = getModel(BookStorageItem.class, "", true);
		String publishDate = getPara("publish_date");
		try {
			bookStorageItem.setPublishDate(CommonKit.parseDate(publishDate, "yyyy-MM"));
			this.logic.saveItem(bookStorageItem);
			ok("保存成功");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("出版日期格式为yyyy-MM");
		}

	}

	/**
	 * 分页查看入库明细（图书列表）
	 */
	public void pageItemInfo(){
		Long bookStorageId = getParaToLong("book_storage_id");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		ok(this.logic.pageItemInfo(bookStorageId, pageNumber, pageSize));
	}

	/**
	 * 删除入库明细
	 * @param id
	 */
	@JsyAddDelEdit(requirePrimary = true, primaryParam = "book_storage_id")
	@Before(TxPost.class)
	public void deleteItem(@Para(value = "id", defaultValue = "0") long id){
		this.logic.deleteItem(id);
		ok("删除成功！");
	}

	/**
	 * 通过名字分页获取书本信息
	 */
	public void pageBook(){
		String bookName = getPara("book_name");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		ok(this.logic.queryByBookName(bookName, pageNumber, pageSize));
	}

	/**
	 * 通过入库id分页查询条形码记录
	 */
	@JsyAddDelEdit(requirePrimary = true, primaryParam = "book_storage_id")
	public void pageBarCode(){
		Long bookStorageId = getParaToLong("book_storage_id");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		ok(this.logic.queryBarCodeByStorageId(bookStorageId, pageNumber, pageSize));

	}

	/**
	 * 通过条形码查询明细
	 */
	public void getItemByBarCode(){
		Long bookStorageId = getParaToLong("book_storage_id");
		String barCode = getPara("bar_code");
		ok(this.logic.queryItemByBarCode(bookStorageId, barCode));
	}

	/**
	 * 确认条形码
	 */
	@JsyAddDelEdit(requirePrimary = true, primaryParam = "book_storage_id")
	public void confirm(){
		Long bookStorageId = getParaToLong("book_storage_id");
		String barCode = getPara("bar_code");
		this.logic.confirm(bookStorageId, barCode);
		ok("确认成功");
	}

	/**
	 * 查看扫码确认情况
	 */
	public void pageConfirmInfo(){
		Long bookStorageId = getParaToLong("book_storage_id");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		ok(this.logic.queryConfirmByStorageId(bookStorageId, pageNumber, pageSize));
	}

	/**
	 * 获取确认、未确认数量
	 */
	public void getCount(){
		Long bookStorageId = getParaToLong("book_storage_id");
		ok(this.logic.getCountByStorageId(bookStorageId));
	}

	/**
	 * 通过入库事件获取入库明细
	 */
	public void getItemByName(){
		String name = getPara("name");
		String beginTime = getPara("begin_time");
		String endTime = getPara("end_time");
		String keyword = getPara("keyword");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		JSONObject data = new JSONObject();
		data.put("total_cnt", this.logic.getItemByNameCnt(name, beginTime, endTime, keyword));
		data.put("total_amount", this.logic.getItemByNameAmount(name, beginTime, endTime, keyword));
		Page<Record> page = this.logic.getItemByName(name, beginTime, endTime, keyword, pageNumber, pageSize);
		data.put("page_number", page.getPageNumber());
		data.put("page_size", page.getPageSize());
		data.put("total_page", page.getTotalPage());
		data.put("total_row", page.getTotalRow());
		data.put("list", page.getList());
		ok("查询入库信息成功", data);

	}

	/**
	 * 导出入库明细
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void excelItemByName(){
		String name = getPara("name");
		String beginTime = getPara("begin_time");
		String endTime = getPara("end_time");
		String keyword = getPara("keyword");
		SXSSFWorkbook wb = this.logic.createExcelItemByName(name, beginTime, endTime, keyword);
		render(new ExcelExport(wb, "入库明细"));

	}

}