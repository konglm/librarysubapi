package com.school.library.borrowsetting;

import com.alibaba.fastjson.JSON;
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
import com.jfnice.model.BorrowSetting;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

@Before(BorrowSettingValidator.class)
public class BorrowSettingController extends JFniceBaseController {

	@Inject
	private BorrowSettingLogic logic;
	@Inject
	private BorrowSettingService service;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void index() {
		ok(logic.queryIndex());
	}

	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		BorrowSetting borrowSetting = getModel(BorrowSetting.class, "", true);
		this.logic.saveInfo(borrowSetting);
		ok("修改成功！");
	}


}