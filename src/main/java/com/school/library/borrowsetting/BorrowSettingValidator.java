package com.school.library.borrowsetting;

import com.jfinal.core.Controller;
import com.jfnice.core.JFniceBaseValidator;

public class BorrowSettingValidator extends JFniceBaseValidator {

	@Override
	protected void validate(Controller c) {
		if (isPost()) {
			switch (getActionName()) {
				case "add":
					validateRequiredString("name", "ERROR_NO_NAME", "请输入名称！");
					break;
				case "edit":
					validateInteger("min_deposit", 0, 100000,"ERROR_MIN_DEPOSIT", "最小押金必须为整数，最小值为0，最大值为1000");
					//validateInteger("max_deposit", 0, 21474836,"ERROR_MAX_DEPOSIT", "最高押金必须为整数，最小值为0，最大值为21474836");
					validateInteger("max_borrow_count", 0, 1000,"ERROR_MAX_BORROW_COUNT", "可借图书数量必须为整数，最小值为0，最大值为1000");
					validateInteger("borrow_days", 0, 1000,"ERROR_BORROW_DAYS", "借阅时间必须为整数，最小值为0，最大值为1000");

					validateInteger("unit_cost", 0, 100000,"ERROR_UNIT_COST", "借阅期间费用必须为整数，最小值为0，最大值为1000");
					validateInteger("first_beyond_days", 0, 1000,"ERROR_FIRST_BEYOND", "超出借阅期必须为整数，最小值为0，最大值为1000");
					validateInteger("first_beyond_unit_cost", 0, 100000,"ERROR_FIRST_BEYOND_COST", "超出借阅期费用必须为整数，最小值为0，最大值为1000");
					validateInteger("second_beyond_days", 0, 1000,"ERROR_SECOND_BEYOND", "超出借阅期必须为整数，最小值为0，最大值为1000");
					validateInteger("second_beyond_unit_cost", 0, 100000,"ERROR_SECOND_BEYOND_COST", "超出借阅期费用必须为整数，最小值为0，最大值为1000");
					validateInteger("max_borrow_cost", 0, 100000,"ERROR_MAX_BORROW_COST", "总费用必须为整数，最小值为0，最大值为1000");

//					validateInteger("beyond_warning_days", 0, 1000,"ERROR_BEYOND_WARNING_DAYS", "超期预警天数必须为整数，最小值为0，最大值为1000");
//					validateInteger("unreturn_warning_days", 0, 1000,"ERROR_UNRETURN_WARNING_DAYS", "未归还预警天数必须为整数，最小值为0，最大值为1000");
//					validateInteger("deposit_warning", 0, 100000,"ERROR_DEPOSIT_WARNING", "押金预警金额必须为整数，最小值为0，最大值为1000");
//					validateInteger("uncheck_warning_days", 0, 1000,"ERROR_UNCHECK_WARNING_DAYS", "未盘点预警天数必须为整数，最小值为0，最大值为1000");
					validateInteger("deposit_audit_flag", 0, 1,"ERROR_DEPOSIT_AUDIT_FLAG", "押金扣除审核必填，且值为0或1");
					break;
				default:
			}
		}
	}

}