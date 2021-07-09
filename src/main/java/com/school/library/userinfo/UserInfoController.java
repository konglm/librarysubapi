package com.school.library.userinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CurrentUser;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.UserInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
import java.util.Objects;

@Before(UserInfoValidator.class)
public class UserInfoController extends JFniceBaseController {

	@Inject
	private UserInfoLogic logic;
	@Inject
	private UserInfoService service;

	/**
	 * 首页查询学生记录
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void pageStu() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		if(Objects.equals("-1", condPara.getStr("grd_code"))){
			condPara.put("grd_code", "");
		}
		if(Objects.equals("-1", condPara.getStr("cls_code"))){
			condPara.put("cls_code", "");
		}
		Page<UserInfo> page = logic.queryIndexPageStu(condPara);
		ok(page);
	}

	/**
	 * 首页查询教师记录
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void pageTeacher() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		if(Objects.equals("-1", condPara.getStr("dpt_code"))){
			condPara.put("dpt_code", "");
		}
		Page<UserInfo> page = logic.queryIndexPageTeacher(condPara);
		ok(page);
	}

	/**
	 * 同步学生信息
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	@Before(TxPost.class)
	public void synchronizeStuInfo() {
		this.logic.synchronizeStuInfo();
		ok("同步成功！");
	}

	/**
	 * 同步教师信息
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	@Before(TxPost.class)
	public void synchronizeTeacherInfo() {
		this.logic.synchronizeTeacherInfo();
		ok("同步成功！");
	}

	/**
	 * 获取用户详细信息
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	@Before(TxPost.class)
	public void getDetail() {
		Long id = getParaToLong("id");
		int pageNumber = getParaToInt("page_number", 1);
		int pageSize = getParaToInt("page_size", 15);
		ok(this.logic.getDetailById(id, pageNumber, pageSize));
	}

	/**
	 * 退还押金
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	@Before(TxPost.class)
	public void returnDeposit() {
		Long id = getParaToLong("id");
		ok(this.logic.returnDeposit(id));
	}

	/**
	 * 暂停/恢复借阅
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	@Before(TxPost.class)
	public void stopOrRecoverBorrow() {
		String userType = getPara("user_type");
		String userCode = getPara("user_code");
		int canBorrow = getInt("can_borrow");
		ok(this.logic.stopOrRecoverBorrow(CurrentUser.getSchoolCode(), userType, userCode, canBorrow));
	}



}