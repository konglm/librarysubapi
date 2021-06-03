package com.school.library.bookinventoryitem;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookInventoryItem;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BookInventoryItemService extends JFniceBaseService<BookInventoryItem> {

	public boolean save(BookInventoryItem bookinventoryitem) {
		boolean flag = super.save(bookinventoryitem);
		BookInventoryItemIdMap.me.clear();
		return flag;
	}

	public boolean update(BookInventoryItem bookinventoryitem) {
		boolean flag = super.update(bookinventoryitem);
		BookInventoryItemIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookinventoryitemId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookinventoryitemId, isRealDelete);
		BookInventoryItemIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookInventoryItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookInventoryItemIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookInventoryItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookInventoryItemIdMap.me.clear();
		}
	}

	/**
	 * 根据条形码表插入入库明细表
	 * @param nowTime
	 * @param userCode
	 * @param createUserName
	 * @param schoolCode
	 * @param bookInventoryId
	 * @param status 未确认状态
	 * @param notIncludeStatus 未计入盘点的条形码状态
	 * @param unReturnStatus 借书未归还的状态
	 * @return
	 */
	public int insertFromBarCode(Date nowTime, String userCode, String createUserName, String schoolCode,
								 Long bookInventoryId, int status, String notIncludeStatus, int unReturnStatus){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("now_time", nowTime);
		condPara.put("user_code", userCode);
		condPara.put("create_user_name", createUserName);
		condPara.put("book_inventory_id", bookInventoryId);
		condPara.put("status", status);
		condPara.put("not_include_status", notIncludeStatus);
		condPara.put("un_return_status", unReturnStatus);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryItemLogic.insertFromBarCode", condPara);
		return Db.update(sqlPara);
	}

	/**
	 * 通过条形码查询盘点明细
	 * @param schoolCode
	 * @param bookInventoryId
	 * @param barCode
	 * @return
	 */
	public List<BookInventoryItem> queryByBarCode(String schoolCode, Long bookInventoryId, String barCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_inventory_id", bookInventoryId);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryItemLogic.queryByBarCode", condPara);
		return BookInventoryItem.dao.find(sqlPara);
	}

	/**
	 * 根据状态统计数量
	 * @param schoolCode
	 * @param bookInventoryId
	 * @return
	 */
	public List<Record> statisticsByStatus(String schoolCode, Long bookInventoryId){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_inventory_id", bookInventoryId);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryItemLogic.statisticsByStatus", condPara);
		return Db.find(sqlPara);
	}

	/**
	 * 分页查询确认明细列表
	 * @param schoolCode
	 * @param bookInventoryId
	 * @param status
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<BookInventoryItem> pageConfirmList(String schoolCode, Long bookInventoryId, int status, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_inventory_id", bookInventoryId);
		condPara.put("status", status);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryItemLogic.queryConfirmList", condPara);
		return BookInventoryItem.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 分页查询未确认明细列表
	 * @param schoolCode
	 * @param bookInventoryId
	 * @param status
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<BookInventoryItem> pageUnConfirmList(String schoolCode, Long bookInventoryId, int status, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_inventory_id", bookInventoryId);
		condPara.put("status", status);
		SqlPara sqlPara = Db.getSqlPara("BookInventoryItemLogic.queryUnConfirmList", condPara);
		return BookInventoryItem.dao.paginate(pageNumber, pageSize, sqlPara);
	}

}