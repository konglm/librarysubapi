package com.school.library.borrowbook;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BorrowBook;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BorrowBookService extends JFniceBaseService<BorrowBook> {

	public boolean save(BorrowBook borrowbook) {
		boolean flag = super.save(borrowbook);
		BorrowBookIdMap.me.clear();
		return flag;
	}

	public boolean update(BorrowBook borrowbook) {
		boolean flag = super.update(borrowbook);
		BorrowBookIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long borrowbookId, boolean isRealDelete) {
		boolean flag = super.deleteById(borrowbookId, isRealDelete);
		BorrowBookIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BorrowBook> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BorrowBookIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BorrowBook> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BorrowBookIdMap.me.clear();
		}
	}

	public <K, V> int[] sort(Map<K, V> map) {
		int[] arr = super.sort(map);
		BorrowBookIdMap.me.clear();
		return arr;
	}

	/**
	 * 获取指定月份的借书数量
	 * @param schoolCode
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public Record statisticsBorrowCnt(String schoolCode, String beginTime, String endTime) {
		Kv kv = Kv.by("school_code", schoolCode).set("begin_time", beginTime).set("end_time", endTime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsBorrowCnt", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

	/**
	 * 获取指定月份的还书数量
	 * @param schoolCode
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public Record statisticsReturnCnt(String schoolCode, String beginTime, String endTime) {
		Kv kv = Kv.by("school_code", schoolCode).set("begin_time", beginTime).set("end_time", endTime);
		SqlPara sqlPara = Db.getSqlPara("BorrowBookLogic.statisticsReturnCnt", kv);
		Record record = Db.findFirst(sqlPara);
		return record;
	}

}