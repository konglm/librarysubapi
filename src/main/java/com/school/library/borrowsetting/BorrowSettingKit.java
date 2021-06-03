package com.school.library.borrowsetting;

import com.jfnice.model.BorrowSetting;
import com.school.library.constants.SysConstants;

import java.util.Date;

/**
 * @Description 借出设置辅助类
 * @Author jsy
 * @Date 2020/3/17
 * @Version V1.0
 **/

public class BorrowSettingKit {

    /**
     * 生成默认的图书外借设置记录
     * @return
     */
    public static BorrowSetting generateDefault(){
        BorrowSetting sysSetting = new BorrowSetting();
        sysSetting.setCreateTime(new Date());
        sysSetting.setCreateUserCode(null);
        sysSetting.setCreateUserName(null);
        sysSetting.setDel(false);
        sysSetting.setUpdateTime(new Date());
        sysSetting.setUpdateUserCode(null);
        sysSetting.setSchoolCode(null);
        sysSetting.setSource(SysConstants.SYS_RECORD_SOURCE);
        sysSetting.setMinDeposit(100);
        sysSetting.setMaxDeposit(10000);
        sysSetting.setMaxBorrowCount(20);
        sysSetting.setBorrowDays(30);
        sysSetting.setUnitCost(0);
        sysSetting.setFirstBeyondDays(10);
        sysSetting.setFirstBeyondUnitCost(2);
        sysSetting.setSecondBeyondDays(30);
        sysSetting.setSecondBeyondUnitCost(3);
        sysSetting.setMaxBorrowCost(10000);
        sysSetting.setBeyondWarningDays(5);
        sysSetting.setUnreturnWarningDays(3);
        sysSetting.setDepositWarning(500);
        sysSetting.setUncheckWarningDays(180);
        sysSetting.setDepositAuditFlag((short)1);
        return sysSetting;
    }

}
