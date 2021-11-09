package com.school.library.book;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.Book;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BookService extends JFniceBaseService<Book> {

	public boolean save(Book book) {
		boolean flag = super.save(book);
		BookIdMap.me.clear();
		return flag;
	}

	public boolean update(Book book) {
		boolean flag = super.update(book);
		BookIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long bookId, boolean isRealDelete) {
		boolean flag = super.deleteById(bookId, isRealDelete);
		BookIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<Book> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			BookIdMap.me.clear();
		}
	}

	public void batchUpdate(List<Book> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			BookIdMap.me.clear();
		}
	}

	/**
	 * 根据书本信息查询书本
	 * @param schoolCode
	 * @param id
	 * @param bookStorageItemId
	 * @param catalogName
	 * @param bookName
	 * @param author
	 * @param publisher
	 * @param publishDate
	 * @return
	 */
	public Book queryByBookInfo(String schoolCode, Long id, Long bookStorageItemId, String catalogName, String bookName,
									  String author, String publisher, Date publishDate){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		condPara.put("book_storage_item_id", bookStorageItemId);
		condPara.put("catalog_name", catalogName);
		condPara.put("book_name", bookName);
		condPara.put("author", author);
		condPara.put("publisher", publisher);
		condPara.put("publish_date", publishDate);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.queryByBookInfo", condPara);
		return Book.dao.findFirst(sqlPara);
	}

	/**
	 * 根据明细删除书本信息
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
		SqlPara sqlPara = Db.getSqlPara("BookLogic.logicDeleteByItemId", condPara);
		return Db.update(sqlPara);
	}

	/**
	 * 通过书名分页查询书本信息
	 * @param schoolCode
	 * @param booName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Book> queryByBookName(String schoolCode, String booName, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("book_name", booName);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.queryByBookName", condPara);
		return Book.dao.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 * 通过条形码查询书本信息
	 * @param schoolCode
	 * @param barCode
	 * @return
	 */
	public Book queryByBarCode(String schoolCode, String barCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.queryByBarCode", condPara);
		return Book.dao.findFirst(sqlPara);
	}

	/**
	 * 通过目录id查询书本信息
	 * @param schoolCode
	 * @param catalogIds
	 * @return
	 */
	public Book queryByCatalogIds(String schoolCode, String catalogIds){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_ids", catalogIds);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.queryByCatalogIds", condPara);
		return Book.dao.findFirst(sqlPara);
	}

	/**
	 * 查询在馆图书
	 * @param schoolCode
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getBooksIn(String schoolCode, int catalogId, String beginTime, String endTime, String keyword, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksIn", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 *
	 * @param schoolCode
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Record getBooksInCnt(String schoolCode, int catalogId, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksInCnt", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 *
	 * @param schoolCode
	 * @param catalogId
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Record getBooksInAmount(String schoolCode, int catalogId, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksInAmount", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 查询在馆图书
	 * @param schoolCode
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getBooksBorrow(String schoolCode, int catalogId, int isOverDay, String beginTime, String endTime, String keyword, int pageNumber, int pageSize){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("is_over_day", isOverDay);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksBorrow", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

	/**
	 *
	 * @param schoolCode
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Record getBooksBorrowCnt(String schoolCode, int catalogId, int isOverDay, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("is_over_day", isOverDay);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksBorrowCnt", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 *
	 * @param schoolCode
	 * @param catalogId
	 * @param isOverDay
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Record getBooksBorrowAmount(String schoolCode, int catalogId, int isOverDay, String beginTime, String endTime, String keyword){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("catalog_id", catalogId);
		condPara.put("is_over_day", isOverDay);
		condPara.put("begin_time", beginTime);
		condPara.put("end_time", endTime);
		condPara.put("keyword", keyword);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBooksBorrowAmount", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 通过条形码获取图书
	 * @param schoolCode
	 * @param barCode
	 * @return
	 */
	public Record getBookInfoByBar(String schoolCode, String barCode) {
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBookInfoByBar", condPara);
		return Db.findFirst(sqlPara);
	}

	/**
	 * 通过条形码获取图书列表
	 * @param schoolCode
	 * @param barCode
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getBookBorrowList(String schoolCode, String barCode, int pageNumber, int pageSize) {
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("bar_code", barCode);
		SqlPara sqlPara = Db.getSqlPara("BookLogic.getBookBorrowList", condPara);
		return Db.paginate(pageNumber, pageSize, sqlPara);
	}

}