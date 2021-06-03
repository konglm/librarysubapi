package com.school.library.bookinventory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.annotation.ShiroClear;
import com.jfnice.enums.OpCodeEnum;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.BookInventory;
import com.school.library.bookinventoryitem.BookInventoryItemStatusEnum;
import com.school.library.constants.DictConstants;
import com.school.library.kit.JsyAddDelEdit;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

/**
 * 图书盘点
 */
@Before({BookInventoryValidator.class, DelEditValidator.class})
public class BookInventoryController extends JFniceBaseController {

	@Inject
	private BookInventoryLogic logic;
	@Inject
	private BookInventoryService service;

	/**
	 * 查询字典
	 */
	@ShiroClear
	public void dict(){
		JSONArray statusArray = JSON.parseArray(DictKit.toJsonArray(DictConstants.INVENTORY_STATUS_TAG));
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
		Page<BookInventory> page = logic.queryIndexPage(condPara);

		ok(page);
	}


	/**
	 * 新建盘点
	 */
	@JsyPermissions(OpCodeEnum.ADD)
	@Before(TxPost.class)
	public void add() {
		this.logic.add();
		ok("保存成功！");
	}

	/**
	 * 删除盘点
	 * @param id
	 */
	@JsyPermissions(value = OpCodeEnum.DELETE, pass = true)
	@JsyAddDelEdit(requirePrimary = true)
	@Before(TxPost.class)
	public void delete(@Para(value = "id", defaultValue = "0") long id) {
		boolean result = this.logic.logicDelete(id);
		if(result){
			ok("删除成功！");
		}else{
			fail("删除失败！");
		}
	}

	/**
	 * 结束盘点
	 * @param id
	 */
	@JsyAddDelEdit(requirePrimary = true)
	public void end(@Para(value = "id", defaultValue = "0") long id) {
		boolean result = this.logic.end(id);
		if(result){
			ok("操作成功！");
		}else{
			fail("操作失败！");
		}
	}

	/**
	 * 通过条形码查询盘点明细信息
	 */
	public void getItemByBarCode(){
		Long bookInventoryId = getParaToLong("book_inventory_id");
		String barCode = getPara("bar_code");
		ok(this.logic.queryItemByBarCode(bookInventoryId, barCode));
	}

	/**
	 * 扫码确认
	 */
	public void confirm(){
		Long bookInventoryId = getParaToLong("book_inventory_id");
		String barCode = getPara("bar_code");
		ok(this.logic.confirm(bookInventoryId, barCode));
	}

	/**
	 * 分页查询已确认明细
	 */
	public void pageConfirmItem(){
		Long bookInventoryId = getParaToLong("book_inventory_id");
		int pageNumber= getParaToInt("page_number", 1);
		int pageSize = getParaToInt("page_size", 15);
		ok(this.logic.queryPageItem(bookInventoryId, BookInventoryItemStatusEnum.CONFIRM.getStatus(), pageNumber, pageSize));
	}

	/**
	 * 分页查询未确认明细
	 */
	public void pageUnConfirmItem(){
		Long bookInventoryId = getParaToLong("book_inventory_id");
		int pageNumber= getParaToInt("page_number", 1);
		int pageSize = getParaToInt("page_size", 15);
		ok(this.logic.queryPageItem(bookInventoryId, BookInventoryItemStatusEnum.UN_CONFIRM.getStatus(), pageNumber, pageSize));
	}

}