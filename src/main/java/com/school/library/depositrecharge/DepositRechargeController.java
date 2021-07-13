package com.school.library.depositrecharge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.ext.ExcelExport;
import com.jfnice.model.BorrowSetting;
import com.jfnice.model.UserInfo;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.DepositRecharge;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.kit.CommonKit;
import com.school.library.userinfo.UserInfoLogic;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Date;
import java.util.Map;

@Before(DepositRechargeValidator.class)
public class DepositRechargeController extends JFniceBaseController {
	
	private final static String ACCESS = "library:DepositRecharge:index";
	private final static String ADD = "library:Operation:add";
	private final static String EDIT = "library:Operation:edit";
	private final static String DELETE = "library:Operation:delete";

	@Inject
	private DepositRechargeLogic logic;
	@Inject
	private DepositRechargeService service;

	@Inject
	private UserInfoLogic userInfoLogic;

	@Inject
	private BorrowSettingLogic settingLogic;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<DepositRecharge> page = logic.queryPage(condPara);

		if (CollectionUtils.isNotEmpty(page.getList())) {
			String res = PtApi.getPermissionByPositionList(new OpCodeEnum[]{OpCodeEnum.EDIT, OpCodeEnum.DELETE});
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

	@JsyPermissions(OpCodeEnum.ADD)
	@Before(TxPost.class)
	public void add() {
		DepositRecharge depositRecharge = getModel(DepositRecharge.class, "", true);
		service.save(depositRecharge);
		ok("保存成功！");
	}

//	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		DepositRecharge depositRecharge = getModel(DepositRecharge.class, "", true);
		service.update(depositRecharge);
		ok("修改成功！");
	}

	@JsyPermissions(OpCodeEnum.DELETE)
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

	/**
	 * 充值押金
	 * @param userType
	 * @param userCode
	 * @param money
	 * @param grd_code
	 * @param grd_name
	 * @param cls_code
	 * @param cls_name
	 * @param stu_code
	 * @param stu_no
	 * @param dept_name
	 * @param user_name
	 * @param card_no
	 * @param schoolCode
	 */
	@Before(Tx.class)
	public void recharge(@Para(value = "user_type") String userType,
						 @Para(value = "user_code") String userCode,
						 @Para(value = "money") double  money,
						 @Para(value = "grd_code") String grd_code,
						 @Para(value = "grd_name") String grd_name,
						 @Para(value = "cls_code") String cls_code,
						 @Para(value = "cls_name") String cls_name,
						 @Para(value = "stu_code") String stu_code,
						 @Para(value = "stu_no") String stu_no,
						 @Para(value = "dpt_name") String dept_name,
						 @Para(value = "user_name") String user_name,
						 @Para(value = "card_no") String card_no,
						 @Para(value = "create_user_code") String CreateUserCode,
						 @Para(value = "create_user_name") String CreateUserName,
						 @Para(value = "unit_code") String schoolCode){
		if(money<0){
			throw new ErrorMsg("金额不能为负数,请重新输入");
		}
		DepositRecharge depositRecharge = getModel(DepositRecharge.class, "", true);
		Integer moneys = CommonKit.formatMoneyToFen(money);
		if(moneys>100000||moneys<0){
			throw new ErrorMsg("充值金额超过最大金额限制!");
		}
		if("stu".equals(userType)){
			userCode = stu_code;
		}
		UserInfo userInfo = userInfoLogic.findByUserCode(schoolCode, userCode, userType);
		if(userInfo.getDeposit()+moneys>10000000){
			throw new ErrorMsg("超出用户最大金额限制,请减少充值!");
		}
		userInfo.setDeposit(userInfo.getDeposit()+moneys);
		depositRecharge.setCreateTime(new Date());
		depositRecharge.setCreateUserCode(CreateUserCode);
		depositRecharge.setCreateUserName(CreateUserName);
		depositRecharge.setDel(false);
		depositRecharge.setRechargeTime(new Date());
		depositRecharge.setRechargeAmount(moneys);
		depositRecharge.setSchoolCode(schoolCode);
		depositRecharge.save();
		userInfo.update();
		ok("充值成功");
	}

	/**
	 * 充值押金记录
	 */
	public void depositList(@Para("keywords") String keywords,
							@Para("start_time") String startTime,
							@Para("end_time") String endTime,
							@Para(value = "page_number", defaultValue = "1") int pageNumber,
							@Para(value = "page_size", defaultValue = "10") int pageSize){
		JSONObject data =  new JSONObject();
		String totalAmount = logic.getTotalDepositAmount(keywords, startTime, endTime);
		data.put("total_amount",  totalAmount);
		Page<Record> page = logic.depositList(keywords, startTime, endTime, pageNumber, pageSize);
		data.put("list",  page);
		ok("查询成功",data);
	}

    /**
     * 导出充值押金记录
     * @param keywords
     * @param startTime
     * @param endTime
     */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void excelDepositList(@Para("keywords") String keywords,
								 @Para("start_time") String startTime,
								 @Para("end_time") String endTime){
		SXSSFWorkbook wb = this.logic.createExcelDepositList(keywords,startTime,endTime);
		render(new ExcelExport(wb, "充值押金记录"));
	}

}