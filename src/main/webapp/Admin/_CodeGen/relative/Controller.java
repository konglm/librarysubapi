package com.jfnice._gen.#(lowercaseModelName);

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ExcelExport;
import com.jfnice.interceptor.TxPost;
import com.jfnice.kit.UrlKit;
import com.jfnice.model.#(modelName);

@Before(#(modelName)Validator.class)
public class #(modelName)Controller extends JFniceBaseController {
	
	@Inject
	private #(modelName)Logic logic;
	@Inject
	private #(modelName)Service #(firstCharLowerModelName)Service;
	
	public void index() {
    	render("index.html");
	}
	
	public void list() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		List<#(modelName)> #(firstCharLowerModelName)List = #(firstCharLowerModelName)Service.getTreeList(condPara.getFields());
		renderJson(#(firstCharLowerModelName)List);
	}
	
	public void excel() {
		CondPara condPara = JsonKit.parse(UrlKit.decodeUrl(getPara("paras")), CondPara.class);
		SXSSFWorkbook wb = logic.createExcel(condPara);
		render(new ExcelExport(wb, "XX表"));
	}
	
	@Before(TxPost.class)
	public void add() {
		if ( !isPost() ) {
			setAttr("#(firstCharLowerModelName)List", #(firstCharLowerModelName)Service.getTreeList("*"));
			long #(firstCharLowerModelName)Id = getParaToLong("id", 0L);
			setAttr("#(firstCharLowerModelName)Id", #(firstCharLowerModelName)Id);
			setAttr("topId", 0L);
			List<Long> parentIds = #(firstCharLowerModelName)Service.queryParentIds(#(firstCharLowerModelName)Id);
			setAttr("parentIds", parentIds);
			render("add.html");
		} else {
			addPost();
		}
	}
	private void addPost() {
		#(modelName) #(firstCharLowerModelName) = getModel(#(modelName).class, "", true);
		#(firstCharLowerModelName)Service.save(#(firstCharLowerModelName));
		ok("添加成功！");
	}
	
	@Before(TxPost.class)
	public void edit() {
		if ( !isPost() ) {
			setAttr("#(firstCharLowerModelName)List", #(firstCharLowerModelName)Service.getTreeList("*"));
			long #(firstCharLowerModelName)Id = getParaToLong("id", 0L);
			#(modelName) #(firstCharLowerModelName) = #(firstCharLowerModelName)Service.queryById(#(firstCharLowerModelName)Id);
			setAttr("#(firstCharLowerModelName)", #(firstCharLowerModelName));
			setAttr("topId", 0L);
			List<Long> parentIds = #(firstCharLowerModelName)Service.queryParentIds(#(firstCharLowerModelName)Id);
			setAttr("parentIds", parentIds);
			render("edit.html");
		} else {
			editPost();
		}
	}
	private void editPost() {
		#(modelName) #(firstCharLowerModelName) = getModel(#(modelName).class, "", true);
		#(firstCharLowerModelName)Service.update(#(firstCharLowerModelName));
		ok("保存成功！");
	}
	
	@Before(Tx.class)
	public void delete() {
		long #(firstCharLowerModelName)Id = getParaToLong("id", 0L);
		#(firstCharLowerModelName)Service.deleteById(#(firstCharLowerModelName)Id, false);
		ok("删除成功！");
	}
	
	@Before(Tx.class)
	public void sort() {
		Map<Long, Long> map = JSON.parseObject(getPara("sorts"), new TypeReference<Map<Long, Long>>(){});
		#(firstCharLowerModelName)Service.sort(map);
		ok("排序成功！");
	}
	
}