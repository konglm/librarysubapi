package com.school.library.bookbarcode;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookBarCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BookBarCodeService extends JFniceBaseService<BookBarCode> {

	public boolean save(BookBarCode bookbarcode) {
		boolean flag = super.save(bookbarcode);
		BookBarCodeIdMap.me.clear();
		return flag;
	}

	public boolean update(BookBarCode bookbarcode) {
		boolean flag = super.update(bookbarcode);
		BookBarCodeIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookbarcodeId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookbarcodeId, isRealDelete);
		BookBarCodeIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookBarCode> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookBarCodeIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookBarCode> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookBarCodeIdMap.me.clear();
		}
	}

	/**
	 * 通过明细id删除条形码记录
	 * @param schoolCode
	 * @param itemId
	 * @return
	 */
	public boolean deleteByItemId(String schoolCode, Long itemId){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_item_id", itemId);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.deleteByItemId", condPara);
		Db.update(sqlPara);
		return true;
	}

	/**
	 * 根据明细id逻辑删除条形码记录
	 * @param schoolCode
	 * @param bookStorageItemId
	 * @param updateUserCode
	 * @param updateTime
	 * @return
	 */
	public int logicDeleteByItemId(String schoolCode, Long bookStorageItemId, String updateUserCode, Date updateTime){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_item_id", bookStorageItemId);
		condPara.put("update_user_code", updateUserCode);
		condPara.put("update_time", updateTime);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.logicDeleteByItemId", condPara);
		return Db.update(sqlPara);
	}

	/**
	 * 通过入库id分页查询条形码记录
	 * @param schoolCode
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<BookBarCode> queryByStorageId(String schoolCode, Long bookStorageId, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.queryByStorageId", condPara);
		return BookBarCode.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 通过条形码查询入库条形码记录
	 * @param schoolCode
	 * @param bookStorageId 入库id
	 * @param barCode
	 * @return
	 */
	public List<BookBarCode> queryByBarCode(String schoolCode, Long bookStorageId, String barCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.queryByBarCode", condPara);
		return BookBarCode.dao.find(sqlPara);
	}

	/**
	 * 根据入库id统计条形码数量，返回未确认总数和已确认总数
	 * @param schoolCode
	 * @param bookStorageId
	 * @param confirmStatus 已确认的状态值
	 * @return
	 */
	public Record statisticsByStorageId(String schoolCode, Long bookStorageId, Integer confirmStatus, Integer unconfirmStatus){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("confirm_status", confirmStatus);
		condPara.put("unconfirm_status", unconfirmStatus);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsByStorageId", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 根据分类号统计图书馆藏数量
	 * @param schoolCode
	 * @return
	 */
	public List<Record> statisticsByCatalogNo(String schoolCode, int confirmStatus){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("confirm_status", confirmStatus);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsByCatalogNo", condPara);
		return Db.find(sqlPara);
	}

	/**
	 * 入库条形码
	 * @param schoolCode
	 * @param bookStorageId
	 * @param updateUserCode
	 * @param updateTime
	 * @param StorageStatus
	 * @return
	 */
	public int storageBarCode(String schoolCode, Long bookStorageId, String updateUserCode, Date updateTime,
							  int StorageStatus, int confirmStatus){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("storage_status", StorageStatus);
		condPara.put("confirm_status", confirmStatus);
		condPara.put("update_user_code", updateUserCode);
		condPara.put("update_time", updateTime);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.storageBarCode", condPara);
		return Db.update(sqlPara);
	}

	/**
	 * 获取馆藏总数
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalCnt(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsTotalCnt", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

	/**
	 * 获取馆藏图书金额
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalAmount(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsTotalAmount", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

	/**
	 * 获取在馆数量
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalIn(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsTotalIn", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

	/**
	 * 获取外借总数
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalOut(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsTotalOut", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

	/**
	 * 获取维修总数
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalRepair(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookBarCodeLogic.statisticsTotalRepair", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

}