package com.school.library.depositrecharge;

import com.jfinal.plugin.activerecord.Db;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.DepositRecharge;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DepositRechargeService extends JFniceBaseService<DepositRecharge> {

	public boolean save(DepositRecharge depositrecharge) {
		if (!isUnique(depositrecharge, "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.save(depositrecharge);
		DepositRechargeIdMap.me.clear();
		return flag;
	}

	public boolean update(DepositRecharge depositrecharge) {
		if (!isUnique(depositrecharge, "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.update(depositrecharge);
		DepositRechargeIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long depositrechargeId, boolean isRealDelete) {
		boolean flag = super.deleteById(depositrechargeId, isRealDelete);
		DepositRechargeIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<DepositRecharge> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			DepositRechargeIdMap.me.clear();
		}
	}

	public void batchUpdate(List<DepositRecharge> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			DepositRechargeIdMap.me.clear();
		}
	}

	public <K, V> int[] sort(Map<K, V> map) {
		int[] arr = super.sort(map);
		DepositRechargeIdMap.me.clear();
		return arr;
	}

}