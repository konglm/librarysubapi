package com.school.library.borrowsetting;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.redis.Redis;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.j2cache.J2CacheKit;
import com.jfnice.model.BorrowSetting;
import com.school.library.constants.RedisConstants;
import com.school.library.constants.SysConstants;
import com.school.library.kit.CommonKit;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class BorrowSettingLogic {

	@Inject
	private BorrowSettingService service;

	/**
	 * 页面查询记录
	 * @return
	 */
	public BorrowSetting queryIndex(){
		BorrowSetting setting = this.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		if(null!= setting){
			//对金额转换为元
			setting.put("min_deposit", CommonKit.formatMoney(setting.getMinDeposit()));
			setting.put("max_deposit", CommonKit.formatMoney(setting.getMaxDeposit()));
			setting.put("unit_cost", CommonKit.formatMoney(setting.getUnitCost()));
			setting.put("first_beyond_unit_cost", CommonKit.formatMoney(setting.getFirstBeyondUnitCost()));
			setting.put("second_beyond_unit_cost", CommonKit.formatMoney(setting.getSecondBeyondUnitCost()));
			setting.put("max_borrow_cost", CommonKit.formatMoney(setting.getMaxBorrowCost()));
			setting.put("deposit_warning", CommonKit.formatMoney(setting.getDepositWarning()));
		}
		return setting;
	}

	/**
	 * 通过学校查询记录，如果没有则根据系统默认生成一条学校新的记录
	 * @param schoolCode
	 * @return
	 */
	public BorrowSetting queryIfNotNewBySchool(String schoolCode){
		//查询学校记录
		BorrowSetting setting = this.service.queryBySchool(schoolCode, SysConstants.SCHOOL_RECORD_SOURCE);
		//学校记录为空
		if(null == setting){
			//批量插入的记录
			List<BorrowSetting> settingList = new ArrayList<>();
			//查询系统默认记录
			BorrowSetting sysSetting = this.service.queryBySource(SysConstants.SYS_RECORD_SOURCE);
			boolean insertSysSetting = false;
			//查询系统默认redis值
			String sysKey = RedisConstants.SYS_BORROW_SETTING_KEY;
			String isSysSetting = J2CacheKit.get(CacheName.DEFAULT_SUB_NAME, sysKey);
			//查询学校redis值
			String key = RedisConstants.BORROW_SETTING_KEY_PREFIX + CurrentUser.getSchoolCode();
			String isSetting = J2CacheKit.get(CacheName.DEFAULT_SUB_NAME, key);
			try {
				//1.系统默认记录为空，则增加一条系统默认记录
				if(null == sysSetting && (StrKit.isBlank(isSysSetting) || "0".equals(isSysSetting) )){
					J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, sysKey, "1", RedisConstants.TIME_TO_LIVE_SECONDS);
					//增加一条系统默认记录
					insertSysSetting = true;
					sysSetting = BorrowSettingKit.generateDefault();
				}

				//2.增加一条学校记录
				if((StrKit.isBlank(isSetting)  || "0".equals(isSetting))){
					J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, "1", RedisConstants.TIME_TO_LIVE_SECONDS);
					//将系统默认记录复制一条到学校记录
					setting = new BorrowSetting();
					setting._setAttrs(sysSetting);
					setting.remove("id");
					setting.setCreateTime(new Date());
					setting.setCreateUserCode(null);
					setting.setCreateUserName(null);
					setting.setDel(false);
					setting.setUpdateTime(new Date());
					setting.setUpdateUserCode(null);
					setting.setSchoolCode(CurrentUser.getSchoolCode());
					setting.setSource(SysConstants.SCHOOL_RECORD_SOURCE);
				}

				BorrowSetting finalSysSetting = sysSetting;
				BorrowSetting finalSetting = setting;
				boolean finalInsertSysSetting = insertSysSetting;
				Db.tx(() -> {
					if(null!= finalSysSetting && finalInsertSysSetting){
						this.service.save(finalSysSetting);
					}
					if(null!= finalSetting){
						this.service.save(finalSetting);
					}
					return true;
				});
			}catch (Exception e){
				J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, sysKey, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
				J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
				e.printStackTrace();
			}


		}
		return setting;
	}

	/**
	 * 根据学校查询记录
	 * @param schoolCode
	 * @return
	 */
	public BorrowSetting queryBySchool(String schoolCode){
		return this.service.queryBySchool(schoolCode, SysConstants.SCHOOL_RECORD_SOURCE);
	}

	/**
	 * 根据数据来源查询记录
	 * @param source
	 * @return
	 */
	public BorrowSetting queryBySource(String source){
		return this.service.queryBySource(source);
	}

	/**
	 * 保存信息
	 * @param setting
	 * @return
	 */
	public void saveInfo(BorrowSetting setting){
		if(null == setting.getId()){
			throw new ErrorMsg("保存失败，请刷新后重试");
		}
		//查出学校记录
		BorrowSetting settingDb = this.service.queryById(CurrentUser.getSchoolCode(), setting.getId());
		//1.学校记录为空
		if(null == settingDb){
			throw new ErrorMsg("保存失败，请刷新后重试");
		}
		//2.学校记录不为空，则进行更新

		setting.setUpdateUserCode(CurrentUser.getUserCode());
		setting.setUpdateTime(new Date());
		setting.setDel(false);
		setting.setCreateUserName(settingDb.getCreateUserName() == null ? CurrentUser.getUserName() : settingDb.getCreateUserName());
		setting.setCreateUserCode(settingDb.getCreateUserCode() == null ? CurrentUser.getUserCode() : settingDb.getCreateUserCode());
		setting.setSource(SysConstants.SCHOOL_RECORD_SOURCE);
		boolean result = this.service.update(setting);
		if(!result){
			throw new ErrorMsg("保存失败，请刷新后重试");
		}
	}

}