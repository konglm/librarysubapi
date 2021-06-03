package com.school.library.search;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.Search;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SearchService extends JFniceBaseService<Search> {

	public boolean save(Search search) {
		boolean flag = super.save(search);
		SearchIdMap.me.clear();
		return flag;
	}

	public boolean update(Search search) {
		boolean flag = super.update(search);
		SearchIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long searchId, boolean isRealDelete) {
		boolean flag = super.deleteById(searchId, isRealDelete);
		SearchIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<Search> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			SearchIdMap.me.clear();
		}
	}

	public void batchUpdate(List<Search> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			SearchIdMap.me.clear();
		}
	}

	/**
	 * 分页查询统计关键词数量
	 * @param schoolCode
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> pageStatisticKeyWord(String schoolCode, String begintime, String endtime, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("begintime", begintime);
		condPara.put("endtime", endtime);
		SqlPara sqlPara = Db.getSqlPara("SearchLogic.statisticsByKeyWord", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

}