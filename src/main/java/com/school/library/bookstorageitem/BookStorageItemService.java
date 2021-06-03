package com.school.library.bookstorageitem;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookStorageItem;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BookStorageItemService extends JFniceBaseService<BookStorageItem> {

	public boolean save(BookStorageItem bookstorageitem) {
		boolean flag = super.save(bookstorageitem);
		BookStorageItemIdMap.me.clear();
		return flag;
	}

	public boolean update(BookStorageItem bookstorageitem) {
		boolean flag = super.update(bookstorageitem);
		BookStorageItemIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookstorageitemId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookstorageitemId, isRealDelete);
		BookStorageItemIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookStorageItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookStorageItemIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookStorageItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookStorageItemIdMap.me.clear();
		}
	}

	/**
	 * 根据id查询学校记录
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public BookStorageItem queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemLogic.queryById", condPara);
		return BookStorageItem.dao.findFirst(sqlPara);
	}

	/**
	 * 通过入库id分页查询入库明细待确认信息
	 * @param schoolCode
	 * @param bookStorageId
	 * @param confirmStatus
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<BookStorageItem> queryConfirmByStorageId(String schoolCode, Long bookStorageId, int confirmStatus,
														 int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("confirm_status", confirmStatus);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemLogic.queryConfirmByStorageId", condPara);
		return BookStorageItem.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 分页查询入库事件里按照名字排名
	 * @param schoolCode
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> queryBarCodeByStorageId(String schoolCode, Long bookStorageId, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemLogic.queryBarCodeByStorageId", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 通过入库id分页查询入库明细信息
	 * @param schoolCode
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<BookStorageItem> queryByStorageId(String schoolCode, Long bookStorageId, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemLogic.queryByStorageId", condPara);
		return BookStorageItem.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 根据条形码查询入库明细
	 * @param schoolCode
	 * @param bookStorageId
	 * @param barCode
	 * @return
	 */
	public List<BookStorageItem> queryByBarCode(String schoolCode, Long bookStorageId, String barCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_storage_id", bookStorageId);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookStorageItemLogic.queryByBarCode", condPara);
		return BookStorageItem.dao.find(sqlPara);
	}


}