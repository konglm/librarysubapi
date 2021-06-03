package com.jfnice._gen.#(lowercaseModelName);

import com.jfinal.plugin.activerecord.Db;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.#(modelName);
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class #(modelName)Service extends JFniceBaseService<#(modelName)> {

	public boolean save(#(modelName) #(lowercaseModelName)) {
		if (!isUnique(#(lowercaseModelName), "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.save(#(lowercaseModelName));
		#(modelName)IdMap.me.clear();
		return flag;
	}

	public boolean update(#(modelName) #(lowercaseModelName)) {
		if (!isUnique(#(lowercaseModelName), "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.update(#(lowercaseModelName));
		#(modelName)IdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long #(lowercaseModelName)Id, boolean isRealDelete) {
		boolean flag = super.deleteById(#(lowercaseModelName)Id, isRealDelete);
		#(modelName)IdMap.me.clear();
		return flag;
	}

	public void batchSave(List<#(modelName)> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			#(modelName)IdMap.me.clear();
		}
	}

	public void batchUpdate(List<#(modelName)> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			#(modelName)IdMap.me.clear();
		}
	}

	public <K, V> int[] sort(Map<K, V> map) {
		int[] arr = super.sort(map);
		#(modelName)IdMap.me.clear();
		return arr;
	}

}