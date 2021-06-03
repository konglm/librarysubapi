package com.school.library.borrowsetting;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BorrowSetting;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BorrowSettingService extends JFniceBaseService<BorrowSetting> {

	@Override
	public boolean save(BorrowSetting borrowsetting) {
		boolean flag = super.save(borrowsetting);
		BorrowSettingIdMap.me.clear();
		return flag;
	}

	@Override
	public boolean update(BorrowSetting borrowsetting) {
		boolean flag = super.update(borrowsetting);
		BorrowSettingIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long borrowsettingId, boolean isRealDelete) {
		boolean flag = super.deleteById(borrowsettingId, isRealDelete);
		BorrowSettingIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BorrowSetting> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BorrowSettingIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BorrowSetting> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BorrowSettingIdMap.me.clear();
		}
	}

	/**
	 * 根据学校查询记录
	 * @param schoolCode
	 * @param source
	 * @return
	 */
	public BorrowSetting queryBySchool(String schoolCode, String source){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("source", source);
		SqlPara sqlPara = Db.getSqlPara("BorrowSettingLogic.queryBySchool", condPara);
		return BorrowSetting.dao.findFirst(sqlPara);
	}

	/**
	 * 根据数据来源查询记录
	 * @param source
	 * @return
	 */
	public BorrowSetting queryBySource(String source){
		CondPara condPara = new CondPara();
		condPara.put("source", source);
		SqlPara sqlPara = Db.getSqlPara("BorrowSettingLogic.queryBySource", condPara);
		return BorrowSetting.dao.findFirst(sqlPara);
	}

	/**
	 * 根据id查询学校记录
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public BorrowSetting queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("BorrowSettingLogic.queryById", condPara);
		return BorrowSetting.dao.findFirst(sqlPara);

	}

}