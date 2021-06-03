package com.school.library.catalog;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BorrowSetting;
import com.jfnice.model.Catalog;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CatalogService extends JFniceBaseService<Catalog> {

	@Override
	public boolean save(Catalog catalog) {
		boolean flag = super.save(catalog);
		CatalogIdMap.me.clear();
		return flag;
	}

	@Override
	public boolean update(Catalog catalog) {
		boolean flag = super.update(catalog);
		CatalogIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long catalogId, boolean isRealDelete) {
		boolean flag = super.deleteById(catalogId, isRealDelete);
		CatalogIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<Catalog> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			CatalogIdMap.me.clear();
		}
	}

	public void batchUpdate(List<Catalog> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			CatalogIdMap.me.clear();
		}
	}

	@Override
	public <K, V> int[] sort(Map<K, V> map) {
		int[] arr = super.sort(map);
		CatalogIdMap.me.clear();
		return arr;
	}

	/**
	 * 通过id查找目录
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public Catalog queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryById", condPara);
		return Catalog.dao.findFirst(sqlPara);
	}

	/**
	 * 查询同父类节点下的节点最大排序号
	 * @param schoolCode
	 * @param pid
	 * @return
	 */
	public Integer queryMaxSortByPid(String schoolCode, Long pid){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("pid", pid);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryMaxSortByPid", condPara);
		Record r = Db.findFirst(sqlPara);
		if(null!= r && null!= r.getInt("sort")){
			return r.getInt("sort");
		}
		return null;
	}

	/**
	 * 根据学校查询记录
	 * @param schoolCode
	 * @param source
	 * @return
	 */
	public List<Catalog> queryBySchool(String schoolCode, String source){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("source", source);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryBySchool", condPara);
		return Catalog.dao.find(sqlPara);
	}

	/**
	 * 根据学校查询已经删除的记录
	 * @param schoolCode
	 * @param source
	 * @return
	 */
	public Catalog queryFirstDelBySchool(String schoolCode, String source){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("source", source);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryDelBySchool", condPara);
		return Catalog.dao.findFirst(sqlPara);
	}

	/**
	 * 根据数据来源查询记录
	 * @param source
	 * @return
	 */
	public List<Catalog> queryBySource(String source){
		CondPara condPara = new CondPara();
		condPara.put("source", source);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryBySource", condPara);
		return Catalog.dao.find(sqlPara);
	}

	/**
	 * 查询同父类节点下的节点最大排序号
	 * @param schoolCode
	 * @param pid
	 * @return
	 */
	public List<Catalog> queryByPid(String schoolCode, Long pid){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("pid", pid);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.queryByPid", condPara);
		return Catalog.dao.find(sqlPara);
	}

	/**
	 * 通过id删除目录
	 * @param schoolCode
	 * @param updateUserCode
	 * @param updateTime
	 * @return
	 */
	public int logicDeleteByIds(String schoolCode, String updateUserCode, Date updateTime, String ids){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("update_user_code", updateUserCode);
		condPara.put("update_time", updateTime);
		condPara.put("ids", ids);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.logicDeleteByIds", condPara);
		return Db.update(sqlPara);
	}
	
}