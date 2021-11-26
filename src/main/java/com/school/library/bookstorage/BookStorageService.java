package com.school.library.bookstorage;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookStorage;
import com.jfnice.model.BorrowSetting;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BookStorageService extends JFniceBaseService<BookStorage> {

	public boolean save(BookStorage bookstorage) {
		boolean flag = super.save(bookstorage);
		BookStorageIdMap.me.clear();
		return flag;
	}

	public boolean update(BookStorage bookstorage) {
		boolean flag = super.update(bookstorage);
		BookStorageIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookstorageId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookstorageId, isRealDelete);
		BookStorageIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookStorage> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookStorageIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookStorage> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookStorageIdMap.me.clear();
		}
	}

	/**
	 * 分页查询首页记录
	 * @return
	 */
	public Page<BookStorage> queryIndexPage(CondPara condPara){
		SqlPara sqlPara = Db.getSqlPara("BookStorageLogic.queryIndexList", condPara);
		return BookStorage.dao.paginate(condPara.getInt("page_number"), condPara.getInt("page_size"), sqlPara);
	}

	/**
	 * 根据id查询学校入库记录
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public BookStorage queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("BookStorageLogic.queryById", condPara);
		return BookStorage.dao.findFirst(sqlPara);

	}

	/**
	 * 根据状态查找入库
	 * @param schoolCode
	 * @param status
	 * @return
	 */
	public List<BookStorage> queryByStatus(String schoolCode, int status){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("status", status);
		SqlPara sqlPara = Db.getSqlPara("BookStorageLogic.queryByStatus", condPara);
		return BookStorage.dao.find(sqlPara);
	}

	/**
	 * 通过条件获取名称
	 * @param schoolCode
	 * @return
	 */
	public List<Record> getNameByLike(String schoolCode, String dateStr, String partName) {
		Kv kv = Kv.by("school_code", schoolCode).set("date_str", dateStr).set("part_name", partName);
		SqlPara sqlPara = Db.getSqlPara("BookStorageLogic.getNameByLike", kv);
		return Db.find(sqlPara);
	}

}