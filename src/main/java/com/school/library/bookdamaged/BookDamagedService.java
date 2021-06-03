package com.school.library.bookdamaged;

import com.jfinal.plugin.activerecord.Db;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookDamaged;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BookDamagedService extends JFniceBaseService<BookDamaged> {

	public boolean save(BookDamaged bookdamaged) {
		if (!isUnique(bookdamaged, "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.save(bookdamaged);
		BookDamagedIdMap.me.clear();
		return flag;
	}

	public boolean update(BookDamaged bookdamaged) {
		if (!isUnique(bookdamaged, "name")) {
			throw new ErrorMsg("名称已存在！");
		}
		boolean flag = super.update(bookdamaged);
		BookDamagedIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookdamagedId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookdamagedId, isRealDelete);
		BookDamagedIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<BookDamaged> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookDamagedIdMap.me.clear();
		}
	}

	public void batchUpdate(List<BookDamaged> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookDamagedIdMap.me.clear();
		}
	}

	public <K, V> int[] sort(Map<K, V> map) {
		int[] arr = super.sort(map);
		BookDamagedIdMap.me.clear();
		return arr;
	}

}