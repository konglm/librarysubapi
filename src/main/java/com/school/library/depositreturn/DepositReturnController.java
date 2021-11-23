package com.school.library.depositreturn;

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
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.ext.ExcelExport;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.DepositRecharge;
import com.jfnice.model.UserInfo;
import com.school.api.gx.PtApi;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.depositrecharge.DepositRechargeLogic;
import com.school.library.depositrecharge.DepositRechargeService;
import com.school.library.depositrecharge.DepositRechargeValidator;
import com.school.library.kit.CommonKit;
import com.school.library.userinfo.UserInfoLogic;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Date;
import java.util.Map;

public class DepositReturnController extends JFniceBaseController {
	
	private final static String ACCESS = "library:DepositReturn:index";
	private final static String ADD = "library:Operation:add";
	private final static String EDIT = "library:Operation:edit";
	private final static String DELETE = "library:Operation:delete";

	@Inject
	private DepositReturnLogic logic;
	@Inject
	private DepositReturnService service;

	@Inject
	private UserInfoLogic userInfoLogic;

	@Inject
	private BorrowSettingLogic settingLogic;

	/**
	 * 充值退还记录
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
		data.put("page_number", page.getPageNumber());
		data.put("page_size", page.getPageSize());
		data.put("total_page", page.getTotalPage());
		data.put("total_row", page.getTotalRow());
		data.put("list", page.getList());
		ok("查询成功",data);
	}

	/**
	 * 导出充值退还记录
	 * @param keywords
	 * @param startTime
	 * @param endTime
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void excelDepositList(@Para("keywords") String keywords,
								 @Para("start_time") String startTime,
								 @Para("end_time") String endTime){
		SXSSFWorkbook wb = this.logic.createExcelDepositList(keywords,startTime,endTime);
		render(new ExcelExport(wb, "押金退还记录"));
	}

}