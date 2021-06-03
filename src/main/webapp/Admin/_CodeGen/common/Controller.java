package com.jfnice._gen.#(lowercaseModelName);

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.#(modelName);
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

@Before(#(modelName)Validator.class)
public class #(modelName)Controller extends JFniceBaseController {
	
	private final static String ACCESS = "#(projectName):#(modelName):index";
	private final static String ADD = "#(projectName):Operation:add";
	private final static String EDIT = "#(projectName):Operation:edit";
	private final static String DELETE = "#(projectName):Operation:delete";

	@Inject
	private #(modelName)Logic logic;
	@Inject
	private #(modelName)Service service;

	@JsyPermissions(ACCESS)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<#(modelName)> page = logic.queryPage(condPara);

		if (CollectionUtils.isNotEmpty(page.getList())) {
			String res = PtApi.getPermissionByPositionList(new String[]{EDIT, DELETE});
			page.getList().forEach(r -> {
				r.put("enable_edit", "1".equals(res.split(",")[0]));
				r.put("enable_delete", "1".equals(res.split(",")[1]));
			});
		}

		ok(page);
	}

	public void index() {
		ok(logic.queryList());
	}

	public void getById(@Para(value = "id", defaultValue = "0") long id) {
		ok(service.queryById(id, "*"));
	}

	@JsyPermissions(ADD)
	@Before(TxPost.class)
	public void add() {
		#(modelName) #(firstCharLowerModelName) = getModel(#(modelName).class, "", true);
		service.save(#(firstCharLowerModelName));
		ok("保存成功！");
	}

	@JsyPermissions(EDIT)
	@Before(TxPost.class)
	public void edit() {
		#(modelName) #(firstCharLowerModelName) = getModel(#(modelName).class, "", true);
		service.update(#(firstCharLowerModelName));
		ok("修改成功！");
	}

	@JsyPermissions(DELETE)
	@Before(TxPost.class)
	public void delete(@Para(value = "id", defaultValue = "0") long id) {
		service.deleteById(id, false);
		ok("删除成功！");
	}

	@Before(Tx.class)
	public void sort() {
		Map<Long, Long> map = JSON.parseObject(getPara("sorts"), new TypeReference<Map<Long, Long>>() {
		});
		service.sort(map);
		ok("排序成功！");
	}

}
