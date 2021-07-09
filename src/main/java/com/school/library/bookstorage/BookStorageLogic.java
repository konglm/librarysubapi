package com.school.library.bookstorage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.jfinal.aop.Inject;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.j2cache.J2CacheKit;
import com.jfnice.model.*;
import com.school.api.gx.PtApi;
import com.school.library.book.BookService;
import com.school.library.bookbarcode.BookBarCodeService;
import com.school.library.bookbarcode.BookBarCodeStatusEnum;
import com.school.library.bookinventory.BookInventoryLogic;
import com.school.library.bookinventory.BookInventoryService;
import com.school.library.bookinventory.BookInventoryStatusEnum;
import com.school.library.bookstorageitem.BookStorageItemService;
import com.school.library.bookstorageitembarcode.BookStorageItemBarCodeKit;
import com.school.library.bookstorageitembarcode.BookStorageItemBarCodeService;
import com.school.library.bookstorageitembarcode.ItemBarCodeStatusEnum;
import com.school.library.catalog.CatalogService;
import com.school.library.constants.DictConstants;
import com.school.library.constants.RedisConstants;
import com.school.library.kit.CommonKit;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class BookStorageLogic {

	@Inject
	private BookStorageService service;
	@Inject
	private CatalogService catalogService;
	@Inject
	private BookService bookService;
	@Inject
	private BookStorageItemService itemService;
	@Inject
	private BookBarCodeService barCodeService;
	@Inject
	private BookInventoryLogic bookInventoryLogic;
	@Inject
	private BookStorageItemBarCodeService itemBarCodeService;

	/**
	 * 分页查询首页记录
	 * @return
	 */
	public Page<BookStorage> queryIndexPage(CondPara condPara){
		condPara.put("school_code", CurrentUser.getSchoolCode());
		Page<BookStorage> page = this.service.queryIndexPage(condPara);
		if (CollectionUtils.isNotEmpty(page.getList())) {
			String res = PtApi.getPermissionByPositionList(new OpCodeEnum[]{OpCodeEnum.DELETE});
			String userCode = CurrentUser.getUserCode();
			boolean tempDelete = "1".equals(res.split(",")[0]);
			page.getList().forEach(r -> {
				boolean enableDelete = tempDelete;
				String createUserCode = r.getCreateUserCode();
				boolean isCreate = false;
				if(StrKit.notBlank(userCode) && userCode.equals( createUserCode)){
					isCreate = true;
				}
				r.put("enable_delete", enableDelete || isCreate) ;
				r.put("is_creator", isCreate) ;
				r.put("create_time", DateKit.toStr(r.getCreateTime(), "yyyy-MM-dd"));
				r.put("status_text", DictKit.text(DictConstants.STORAGE_STATUS_TAG, r.getStatus()));
			});
		}

		return page;
	}

	/**
	 * 保存图书入库事件
	 * @return
	 */
	public BookStorage save(){
		//判断是否有盘点，有盘点则不能入库
		boolean goingInventory = this.bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
		if(goingInventory){
			throw new ErrorMsg("存在尚未结束的盘点任务，不能新建入库");
		}
		String partName = "图书入库";
		String dateStr = DateKit.toStr(new Date(), "yyyy年MM月dd日");
		//自动生成名字
		StringBuilder nameBuild = new StringBuilder();
		nameBuild.append(dateStr);
		//redis计数，生成入库次数
		String key = RedisConstants.BOOK_STORAGE_COUNT_KEY_PREFIX + CurrentUser.getSchoolCode() + "_" + dateStr;
		Integer count = J2CacheKit.get(CacheName.DEFAULT_SUB_NAME, key);
		J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, null == count ? 1 : (count + 1), RedisConstants.TIME_TO_LIVE_SECONDS);
		if(null != count){
			nameBuild.append("（").append(count).append("）");
		}
		nameBuild.append(partName);
		BookStorage s = new BookStorage();
		s.setCreateTime(new Date());
		s.setCreateUserCode(CurrentUser.getUserCode());
		s.setCreateUserName(CurrentUser.getUserName());
		s.setDel(false);
		s.setName(nameBuild.toString());
		s.setSchoolCode(CurrentUser.getSchoolCode());
		s.setStatus(StorageStatusEnum.GOING.getStatus());
		s.setUpdateTime(new Date());
		s.setUpdateUserCode(CurrentUser.getUserCode());
		this.service.save(s);
		return s;
	}

	/**
	 * 逻辑删除记录
	 * @param id
	 * @return
	 */
	public boolean logicDelete(Long id){
		BookStorage s = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null == s || s.getStatus() != StorageStatusEnum.GOING.getStatus()){
			return false;
		}
		s.setUpdateUserCode(CurrentUser.getUserCode());
		s.setUpdateTime(new Date());
		s.setDel(true);
		this.service.update(s);
		return true;
	}

	/**
	 * 结束入库
	 * @param id
	 * @return
	 */
	public boolean over(Long id){
		BookStorage s = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null == s){
			return false;
		}
		Boolean result = Db.tx(() -> {
			s.setUpdateUserCode(CurrentUser.getUserCode());
			s.setUpdateTime(new Date());
			s.setStatus(StorageStatusEnum.END.getStatus());
			this.service.update(s);
			//要将条形码的确认状态改为入库状态
			this.barCodeService.storageBarCode(CurrentUser.getSchoolCode(), id, CurrentUser.getUserCode(),new Date(),
					BookBarCodeStatusEnum.STORAGE.getStatus(), BookBarCodeStatusEnum.CONFIRM.getStatus());
			return true;
		});
		return result;
	}

	/**
	 * 保存入库明细
	 * @param item
	 */
	public void saveItem(BookStorageItem item){
		//要保存进入4个表，book(书本信息表)，book_storage_item(入库明细表)，book_storage_item_bar_code（入库明细条形码表），book_bar_code(条形码表)
		//查出目录名
		Catalog c = this.catalogService.queryById(CurrentUser.getSchoolCode(), item.getCatalogId());
		if(null == c){
			throw new ErrorMsg("目录信息不存在，保存失败");
		}
		//查看入库状态，已入库则不能进行更改
		BookStorage storage = this.service.queryById(CurrentUser.getSchoolCode(), item.getBookStorageId());
		if(null == storage || StorageStatusEnum.GOING.getStatus() != storage.getStatus()){
			throw new ErrorMsg("不是入库状态，保存失败");
		}
		String catalogName = c.getCatalogName();
		String catalogNo = c.getCatalogNo();
		//判断此次入库的书本信息是不是唯一的
		item.setSchoolCode(CurrentUser.getSchoolCode());
		if(!this.itemService.isUnique(item, "school_code", "book_storage_id", "book_name", "catalog_name", "author",
				"publisher", "publish_date")){
			throw new ErrorMsg("重复录入相同信息，保存失败");
		}
		Book book = null;
		boolean updateBook = true;
		//生成索书号
		String data = item.getBookName() + item.getAuthor() + item.getPublisher() +
				DateKit.toStr(item.getPublishDate(), "yyyy-MM");
		String bookOrder = CommonKit.MD5(data);
		String checkNo = BookStorageItemBarCodeKit.generateCheckNo(CurrentUser.getSchoolCode(), c.getCatalogNo(), bookOrder);
		//新增的入库条形码的数量
		int addItemBarCodeNum = 0;
		//新增的入库条形码的列表
		List<BookStorageItemBarCode> addItemBarCodeList = new ArrayList<>();
		//编辑的入库条形码的列表（包括更新、删除）
		List<BookStorageItemBarCode> updateItemBarCodeList = new ArrayList<>();
		//1.新增
		if(null == item.getId()){
			item.remove("id");
			addItemBarCodeNum = item.getBookNum();
			//查看书本信息是否存在，不存在则新增
			//书本信息
			updateBook = false;
			book = this.bookService.queryByBookInfo(CurrentUser.getSchoolCode(), null, null, catalogName,
					item.getBookName(), item.getAuthor(), item.getPublisher(), item.getPublishDate());
			if(null == book){
				updateBook = true;
				book = new Book();
				book.setCreateTime(new Date());
				book.setCreateUserCode(CurrentUser.getUserCode());
				book.setCreateUserName(CurrentUser.getUserName());
				book.setDel(false);
				book.setSchoolCode(CurrentUser.getSchoolCode());
			}
			//入库明细信息
			item.setCatalogName(catalogName);
			item.setCatalogNo(catalogNo);
			item.setCreateTime(new Date());
			item.setCreateUserCode(CurrentUser.getUserCode());
			item.setCreateUserName(CurrentUser.getUserName());
			item.setDel(false);
			item.setSchoolCode(CurrentUser.getSchoolCode());
			item.setUpdateTime(new Date());
			item.setUpdateUserCode(CurrentUser.getUserCode());
		}else{
			//编辑，如果有对应的book信息，则一并进行修改，否则新增书本；删除条形码，然后按照入库明细条形码生成条形码
			//书本信息
			book = this.bookService.queryByBookInfo(CurrentUser.getSchoolCode(), null, item.getId(), null,
					null, null, null, null);
			if(null == book){
				book = this.bookService.queryByBookInfo(CurrentUser.getSchoolCode(), null, null, catalogName,
						item.getBookName(), item.getAuthor(), item.getPublisher(), item.getPublishDate());
				updateBook = false;
				if(null == book){
					book = new Book();
					book.setCreateTime(new Date());
					book.setCreateUserCode(CurrentUser.getUserCode());
					book.setCreateUserName(CurrentUser.getUserName());
					book.setDel(false);
					book.setSchoolCode(CurrentUser.getSchoolCode());
					updateBook = true;
				}
			}

			//查询入库明细信息
			BookStorageItem itemDb = this.itemService.queryById(CurrentUser.getSchoolCode(), item.getId());
			item.setCatalogName(catalogName);
			item.setCreateTime(itemDb.getCreateTime());
			item.setCreateUserCode(itemDb.getCreateUserCode());
			item.setCreateUserName(itemDb.getCreateUserName());
			item.setDel(false);
			item.setSchoolCode(CurrentUser.getSchoolCode());
			item.setUpdateTime(new Date());
			item.setUpdateUserCode(CurrentUser.getUserCode());

			//查询入库条形码
			updateItemBarCodeList = this.itemBarCodeService.queryByItemId(CurrentUser.getSchoolCode(), item.getId());
			//删除的数量
			int deleteNum = updateItemBarCodeList.size() - item.getBookNum();
			addItemBarCodeNum = item.getBookNum() - updateItemBarCodeList.size();
			for(int i = 0; i < deleteNum; i++){
				updateItemBarCodeList.get(i).setDel(true);
			}
			//更新入库条形码记录
			updateItemBarCodeList.forEach(b -> {
				b.setPrice(item.getPrice());
				b.setCheckNo(checkNo);
			});
		}

		//更新书本信息
		if(updateBook){
			book.setUpdateTime(new Date());
			book.setUpdateUserCode(CurrentUser.getUserCode());
			book.setAuthor(item.getAuthor());
			book.setBookImgUrl(item.getBookImgUrl());
			book.setBookName(item.getBookName());
			book.setCatalogId(item.getCatalogId());
			book.setCatalogNo(catalogNo);
			book.setCatalogName(item.getCatalogName());
			book.setPublishDate(item.getPublishDate());
			book.setPublisher(item.getPublisher());
			book.setCheckNo(checkNo);
		}
		//新增的入库条形码信息
		if(addItemBarCodeNum > 0){
			String[] barCodes = BookStorageItemBarCodeKit.generateBarCode(CurrentUser.getSchoolCode(), addItemBarCodeNum);
			for(int i = 0; i < addItemBarCodeNum; i++){
				BookStorageItemBarCode barCode = new BookStorageItemBarCode();
				barCode.setBookStorageId(item.getBookStorageId());
				barCode.setPrice(item.getPrice());
				barCode.setCreateTime(new Date());
				barCode.setCreateUserCode(CurrentUser.getUserCode());
				barCode.setCreateUserName(CurrentUser.getUserName());
				barCode.setDel(false);
				barCode.setSchoolCode(CurrentUser.getSchoolCode());
				barCode.setUpdateTime(new Date());
				barCode.setUpdateUserCode(CurrentUser.getUserCode());
				barCode.setStatus(BookBarCodeStatusEnum.UNCONFIRM.getStatus());
				//生成条形码
				barCode.setBarCode(barCodes[i]);
				barCode.setCheckNo(checkNo);

				addItemBarCodeList.add(barCode);
			}
		}


		//事务操作
		Book finalBook = book;
		boolean finalUpdateBook = updateBook;
		List<BookStorageItemBarCode> finalUpdateItemBarCodeList = updateItemBarCodeList;
		Db.tx(() -> {
			//先保存明细信息，然后保存书本信息，最后保存条码信息
			if(null == item.getId()){
				this.itemService.save(item);
			}else{
				this.itemService.update(item);
				//删除原来的条码信息
				this.barCodeService.deleteByItemId(CurrentUser.getSchoolCode(), item.getId());
			}

			if(finalUpdateBook){
				if(null == finalBook.getId()){
					finalBook.setBookStorageItemId(item.getId());
					this.bookService.save(finalBook);
				}else{
					this.bookService.update(finalBook);
				}
			}

			//需要新增的条形码记录
			List<BookBarCode> bookBarCodeList = new ArrayList<>();
			//将更新且未被删除的入库明细条形码信息复制到条形码记录
			finalUpdateItemBarCodeList.forEach(b -> {
				if(!b.getDel()){
					BookBarCode bookBarCode = new BookBarCode();
					for(Map.Entry<String, Object> entry: b._getAttrsEntrySet()){
						bookBarCode.set(entry.getKey(), entry.getValue());
					}
					bookBarCode.remove("id");
					bookBarCodeList.add(bookBarCode);
				}
			});
			//将新增的入库明细条形码信息复制到条形码记录
			addItemBarCodeList.forEach(b -> {
				b.setBookStorageItemId(item.getId());
				b.setBookId(finalBook.getId());
				BookBarCode bookBarCode = new BookBarCode();
				for(Map.Entry<String, Object> entry: b._getAttrsEntrySet()){
					bookBarCode.set(entry.getKey(), entry.getValue());
				}
				bookBarCodeList.add(bookBarCode);
			});
			this.itemBarCodeService.batchUpdate(finalUpdateItemBarCodeList);
			this.itemBarCodeService.batchSave(addItemBarCodeList);
			this.barCodeService.batchSave(bookBarCodeList);

			return true;
		});

	}

	/**
	 * 通过入库id分页查询入库明细待确认信息
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public JSONObject pageItemInfo(Long bookStorageId, int pageNumber, int pageSize) {
		//入库明细分页
		Page<BookStorageItem> page = this.itemService.queryByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				pageNumber, pageSize);
		JSONObject data = new JSONObject();
		page.getList().forEach(i -> {
			i.put("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
		});
		Record totalRecord = this.itemBarCodeService.statisticsTotalByStorageId(CurrentUser.getSchoolCode(), bookStorageId);

		data.put("list", page.getList());
		data.put("page_number", page.getPageNumber());
		data.put("page_size", page.getPageSize());
		data.put("total_page", page.getTotalPage());
		data.put("total_row", page.getTotalRow());
		data.put("total_count", totalRecord.getInt("total_count"));
		return data;
	}


	/**
	 * 删除入库明细
	 * @param id
	 */
	public void deleteItem(Long id){
		BookStorageItem s = this.itemService.queryById(CurrentUser.getSchoolCode(), id);
		if(null == s){
			throw new ErrorMsg("删除失败");
		}
		//查看入库状态，已入库则不能进行更改
		if(StorageStatusEnum.GOING.getStatus() != s.getInt("status")){
			throw new ErrorMsg("不是入库状态，删除失败");
		}
		//将明细、明细对应book、入库明细条形码、条形码四个表的数据删除
		s.setDel(true);
		s.setUpdateTime(new Date());
		s.setUpdateUserCode(CurrentUser.getUserCode());
		Db.tx(() -> {
			this.itemService.update(s);
			this.bookService.logicDeleteByItemId(CurrentUser.getSchoolCode(), id, CurrentUser.getUserCode(), new Date());
			this.itemBarCodeService.logicDeleteByItemId(CurrentUser.getSchoolCode(), id, CurrentUser.getUserCode(), new Date());
			this.barCodeService.logicDeleteByItemId(CurrentUser.getSchoolCode(), id, CurrentUser.getUserCode(), new Date());
			return true;
		});
	}

	/**
	 * 通过书名分页查询书本信息
	 * @param bookName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Book> queryByBookName(String bookName, int pageNumber, int pageSize){
		if(StrKit.isBlank(bookName)){
			return new Page(new ArrayList(), pageNumber, pageSize, 0, 0);
		}
		Page<Book> page = this.bookService.queryByBookName(CurrentUser.getSchoolCode(), bookName, pageNumber, pageSize);
		page.getList().stream().forEach(b -> {
			b.put("price", CommonKit.formatMoney(b.getInt("price")));
			b.put("publish_date", DateKit.toStr(b.getPublishDate(), "yyyy-MM"));
		});
		return page;
	}

	/**
	 * 通过入库id分页查询条形码记录
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> queryBarCodeByStorageId(Long bookStorageId, int pageNumber, int pageSize){
		Page<Record> page =  this.itemService.queryBarCodeByStorageId(CurrentUser.getSchoolCode(), bookStorageId, pageNumber, pageSize);
		page.getList().forEach(r -> {
			List bar_codes = Splitter.on(",").omitEmptyStrings().splitToList(r.getStr("bar_codes"));
			r.set("bar_codes", bar_codes);
			List check_nos = Splitter.on(",").omitEmptyStrings().splitToList(r.getStr("check_nos"));
			r.set("check_nos", check_nos);
			r.set("publish_date", DateKit.toStr(r.getDate("publish_date"), "yyyy-MM"));
		});
		return page;
	}

	/**
	 * 确认条形码
	 * @param bookStorageId
	 * @param barCode
	 */
	public void confirm(Long bookStorageId, String barCode){
		List<BookStorageItemBarCode> codeList = this.itemBarCodeService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		List<BookBarCode> barCodeList = this.barCodeService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		if(null == codeList || codeList.isEmpty()){
			throw new ErrorMsg("此条形码不在本次入库书籍里，确认失败");
		}
		if(codeList.size() > 1){
			throw new ErrorMsg("此条形码重复，请删除本条入库信息，重新录入入库");
		}
		if(StorageStatusEnum.GOING.getStatus() != codeList.get(0).getInt("storage_status")){
			throw new ErrorMsg("此次入库已结束，不能进行确认操作");
		}
		if(ItemBarCodeStatusEnum.CONFIRM.getStatus() == codeList.get(0).getStatus().intValue()){
			throw new ErrorMsg("此条形码已被确认过");
		}
		Db.tx(() -> {
			BookStorageItemBarCode code = codeList.get(0);
			code.setStatus(ItemBarCodeStatusEnum.CONFIRM.getStatus());
			code.setUpdateTime(new Date());
			code.setUpdateUserCode(CurrentUser.getUserCode());
			this.itemBarCodeService.update(code);

			BookBarCode bookBarCode = barCodeList.get(0);
			bookBarCode.setStatus(BookBarCodeStatusEnum.CONFIRM.getStatus());
			bookBarCode.setUpdateTime(new Date());
			bookBarCode.setUpdateUserCode(CurrentUser.getUserCode());
			this.barCodeService.update(bookBarCode);
			return true;
		});
	}

	/**
	 * 通过入库id分页查询入库明细已确认信息
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public JSONObject queryConfirmByStorageId(Long bookStorageId, int pageNumber, int pageSize){
		JSONObject json = new JSONObject();
		//入库明细分页
		Page<BookStorageItem> page = this.itemService.queryConfirmByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				ItemBarCodeStatusEnum.CONFIRM.getStatus(), pageNumber, pageSize);
		JSONArray array = new JSONArray();
		page.getList().forEach(i -> {
			i.put("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
		});
		//入库明细总计
		Record totalRecord = this.itemBarCodeService.statisticsByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				ItemBarCodeStatusEnum.CONFIRM.getStatus(), ItemBarCodeStatusEnum.UNCONFIRM.getStatus());
		json.put("list", page.getList());
		json.put("total_page", page.getTotalPage());
		json.put("total_row", page.getTotalRow());
		json.put("page_number", page.getPageNumber());
		json.put("page_size", page.getPageSize());
		json.put("statistics", totalRecord);
		return json;
	}

	/**
	 * 通过条形码查询明细
	 * @param bookStorageId
	 * @param barCode
	 * @return
	 */
	public BookStorageItem queryItemByBarCode(Long bookStorageId, String barCode){
		List<BookStorageItem> itemList = this.itemService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		if(CollectionUtils.isEmpty(itemList)){
			throw new ErrorMsg("此次入库没有此条形码");
		}
		if(itemList.size() > 1){
			throw new ErrorMsg("此条形码重复，请重新入库更换新的条形码");
		}
		itemList.forEach(i -> {
			i.set("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
		});
		return itemList.get(0);
	}

	/**
	 * 通过入库id查询确认、未确认数量
	 * @param bookStorageId
	 * @return
	 */
	public JSONObject getCountByStorageId(Long bookStorageId){
		JSONObject json = new JSONObject();
		//入库明细总计
		Record totalRecord = this.itemBarCodeService.statisticsByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				ItemBarCodeStatusEnum.CONFIRM.getStatus(), ItemBarCodeStatusEnum.UNCONFIRM.getStatus());
		json.put("count", totalRecord);
		return json;
	}

	/**
	 * 通过入库事件获取入库明细
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param keyword
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> getItemByName(String name, String beginTime, String endTime, String keyword, int pageNumber, int pageSize){
		Page<Record> items = this.itemBarCodeService.getItemByName(CurrentUser.getSchoolCode(), name, beginTime, endTime, keyword, pageNumber, pageSize);
		return items;
	}

}