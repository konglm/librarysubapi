package com.school.library.depositreturn;

import com.jfinal.plugin.activerecord.Db;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.DepositReturn;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DepositReturnService extends JFniceBaseService<DepositReturn> {

	public boolean save(DepositReturn depositreturn) {
		boolean flag = super.save(depositreturn);
		DepositReturnIdMap.me.clear();
		return flag;
	}

	public boolean update(DepositReturn depositreturn) {
		boolean flag = super.update(depositreturn);
		DepositReturnIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long depositreturnId, boolean isRealDelete) {
		boolean flag = super.deleteById(depositreturnId, isRealDelete);
		DepositReturnIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<DepositReturn> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			DepositReturnIdMap.me.clear();
		}
	}

	public void batchUpdate(List<DepositReturn> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			DepositReturnIdMap.me.clear();
		}
	}


}