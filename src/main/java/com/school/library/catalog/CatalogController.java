package com.school.library.catalog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.enums.OpCodeEnum;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.Catalog;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

@Before(CatalogValidator.class)
public class CatalogController extends JFniceBaseController {

	@Inject
	private CatalogLogic logic;
	@Inject
	private CatalogService service;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void index() {
		ok(logic.queryIndex());
	}

	@JsyPermissions(OpCodeEnum.ADD)
	@Before(TxPost.class)
	public void add() {
		this.saveInfo();
	}

	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		this.saveInfo();
	}

	/**
	 * 统一保存方法
	 */
	private void saveInfo(){
		Catalog catalog = getModel(Catalog.class, "", true);
		this.logic.saveInfo(catalog);
		ok("保存成功！");
	}

	/**
	 * 删除
	 * @param id
	 */
	@JsyPermissions(OpCodeEnum.DELETE)
	@Before(TxPost.class)
	public void delete(@Para(value = "id", defaultValue = "0") long id) {
		this.logic.logicDelete(id);
		ok("删除成功！");
	}

	/**
	 * 排序
	 */
	@Before(Tx.class)
	public void sort() {
		Map<Long, Long> map = JSON.parseObject(getPara("sorts"), new TypeReference<Map<Long, Long>>() {
		});
		service.sort(map);
		ok("排序成功！");
	}

	/**
	 * 查询目录是否被使用过
	 * @param id
	 */
	public void getUsed(@Para(value = "id", defaultValue = "0") long id){
		int used = this.logic.getUsed(id);
		JSONObject data = new JSONObject();
		data.put("used", used);
		ok(data);
	}

}