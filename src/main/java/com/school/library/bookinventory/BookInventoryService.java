package com.school.library.bookinventory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookInventory;
import com.jfnice.model.BookStorage;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BookInventoryService extends JFniceBaseService<BookInventory> {

	public boolean save(BookInventory bookinventory) {
		boolean flag = super.save(bookinventory);
		BookInventoryIdMap.me.clear();
		return flag;
	}

	public boolean update(BookInventory bookinventory) {
		boolean flag = super.update(bookinventory);
		BookInventoryIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookinventoryId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookinventoryId, isRealDelete);
		BookInventoryIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookInventory> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookInventoryIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookInventory> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookInventoryIdMap.me.clear();
		}
	}

	/**
	 * 分页查询首页记录
	 * @return
	 */
	public Page<BookInventory> queryIndexPage(CondPara condPara){
		SqlPara sqlPara = Db.getSqlPara("BookInventoryLogic.queryIndexList", condPara);
		return BookInventory.dao.paginate(condPara.getInt("page_number"), condPara.getInt("page_size"), sqlPara);
	}

	/**
	 * 通过状态查询记录
	 * @param schoolCode
	 * @param status
	 * @return
	 */
	public List<BookInventory> queryByStatus(String schoolCode, int status){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("status", status);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryLogic.queryByStatus", condPara);
		return BookInventory.dao.find(sqlPara);
	}

	/**
	 * 通过id查询记录
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public BookInventory queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryLogic.queryById", condPara);
		return BookInventory.dao.findFirst(sqlPara);
	}

}