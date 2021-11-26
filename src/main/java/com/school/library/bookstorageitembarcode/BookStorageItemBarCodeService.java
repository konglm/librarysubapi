package com.school.library.bookstorageitembarcode;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.model.BookStorageItemBarCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;

public class BookStorageItemBarCodeService extends JFniceBaseService<BookStorageItemBarCode> {

	public boolean save(BookStorageItemBarCode bookbarcode) {
		boolean flag = super.save(bookbarcode);
		BookStorageItemBarCodeIdMap.me.clear();
		return flag;
	}

	public boolean update(BookStorageItemBarCode bookbarcode) {
		boolean flag = super.update(bookbarcode);
		BookStorageItemBarCodeIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookbarcodeId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookbarcodeId, isRealDelete);
		BookStorageItemBarCodeIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookStorageItemBarCode> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookStorageItemBarCodeIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookStorageItemBarCode> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookStorageItemBarCodeIdMap.me.clear();
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
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.deleteByItemId", condPara);
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
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.logicDeleteByItemId", condPara);
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
	public Page<BookStorageItemBarCode> queryByStorageId(String schoolCode, Long bookStorageId, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.queryByStorageId", condPara);
		return BookStorageItemBarCode.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 通过条形码查询入库条形码记录
	 * @param schoolCode
	 * @param bookStorageId 入库id
	 * @param barCode
	 * @return
	 */
	public List<BookStorageItemBarCode> queryByBarCode(String schoolCode, Long bookStorageId, String barCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.queryByBarCode", condPara);
		return BookStorageItemBarCode.dao.find(sqlPara);
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
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.statisticsByStorageId", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 根据入库id统计条形码总数量
	 * @param schoolCode
	 * @param bookStorageId
	 * @return
	 */
	public Record statisticsTotalByStorageId(String schoolCode, Long bookStorageId){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.statisticsTotalByStorageId", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 根据分类号统计图书数量
	 * @param schoolCode
	 * @return
	 */
	public List<Record> statisticsByCatalogNo(String schoolCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.statisticsByCatalogNo", condPara);
		return Db.find(sqlPara);
	}

	/**
	 * 根据入库明细id查询条码记录
	 * @param schoolCode
	 * @return
	 */
	public List<BookStorageItemBarCode> queryByItemId(String schoolCode, Long BookStorageItemId){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_item_id", BookStorageItemId);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.queryByItemId", condPara);
		return BookStorageItemBarCode.dao.find(sqlPara);
	}

	/**
	 * 通过入库事件获取入库明细
	 * @param schoolCode
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getItemByName(String schoolCode, String name, String beginTime, String endTime, String keyword, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("name", name);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.getItemByName", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 通过入库事件获取入库数量
	 * @param schoolCode
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public Record getItemByNameCnt(String schoolCode, String name, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("name", name);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.getItemByNameCnt", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 通过入库事件获取入库金额
	 * @param schoolCode
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public Record getItemByNameAmount(String schoolCode, String name, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("name", name);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.getItemByNameAmount", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 获取入库总数
	 * @param schoolCode
	 * @return
	 */
	public Record statisticsTotalStorage(String schoolCode) {
		Kv kv = Kv.by("school_code", schoolCode);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemBarCodeLogic.statisticsTotalStorage", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}
}