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
import com.jfnice.cache.JsyCacheKit;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.SimpleDateFormat;
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
	 * ????????????????????????
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
	 * ????????????????????????
	 * @return
	 */
	public BookStorage save(){
		//????????????????????????????????????????????????
		boolean goingInventory = this.bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
		if(goingInventory){
			throw new ErrorMsg("??????????????????????????????????????????????????????");
		}
		String partName = "????????????";
		String dateStr = DateKit.toStr(new Date(), "yyyy???MM???dd???");

		List<Record> records = service.getNameByLike(CurrentUser.getSchoolCode(), dateStr, partName);
		int index = records.size(); //????????????????????????????????????
		//??????????????????
		StringBuilder nameBuild = new StringBuilder();
		nameBuild.append(dateStr);
		//redis???????????????????????????
		String key = RedisConstants.BOOK_STORAGE_COUNT_KEY_PREFIX + CurrentUser.getSchoolCode() + "_" + dateStr;
		Integer count = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, key);
		if(((count != null) && (index > count)) || ((count == null) && (index > 0))) { //????????????
			count = index;
		}
		JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, key, null == count ? 1 : (count + 1), RedisConstants.TIME_TO_LIVE_SECONDS);
		if(null != count){
			nameBuild.append("???").append(count).append("???");
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
	 * ??????????????????
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
	 * ????????????
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
			//????????????????????????????????????????????????
			this.barCodeService.storageBarCode(CurrentUser.getSchoolCode(), id, CurrentUser.getUserCode(),new Date(),
					BookBarCodeStatusEnum.STORAGE.getStatus(), BookBarCodeStatusEnum.CONFIRM.getStatus());
			return true;
		});
		return result;
	}

	/**
	 * ??????????????????
	 * @param item
	 */
	public void saveItem(BookStorageItem item){
		//???????????????4?????????book(???????????????)???book_storage_item(???????????????)???book_storage_item_bar_code?????????????????????????????????book_bar_code(????????????)
		//???????????????
		Catalog c = this.catalogService.queryById(CurrentUser.getSchoolCode(), item.getCatalogId());
		if(null == c){
			throw new ErrorMsg("????????????????????????????????????");
		}
		//???????????????????????????????????????????????????
		BookStorage storage = this.service.queryById(CurrentUser.getSchoolCode(), item.getBookStorageId());
		if(null == storage || StorageStatusEnum.GOING.getStatus() != storage.getStatus()){
			throw new ErrorMsg("?????????????????????????????????");
		}
		String catalogName = c.getCatalogName();
		String catalogNo = c.getCatalogNo();
		//???????????????????????????????????????????????????
		item.setSchoolCode(CurrentUser.getSchoolCode());
		if(!this.itemService.isUnique(item, "school_code", "book_storage_id", "book_name", "catalog_name", "author",
				"publisher", "publish_date")){
			throw new ErrorMsg("???????????????????????????????????????");
		}
		Book book = null;
		boolean updateBook = true;
		//???????????????
		String data = item.getBookName() + item.getAuthor() + item.getPublisher() +
				DateKit.toStr(item.getPublishDate(), "yyyy-MM");
		String bookOrder = CommonKit.MD5(data);
		String checkNo = BookStorageItemBarCodeKit.generateCheckNo(CurrentUser.getSchoolCode(), c.getCatalogNo(), bookOrder);
		//?????????????????????????????????
		int addItemBarCodeNum = 0;
		//?????????????????????????????????
		List<BookStorageItemBarCode> addItemBarCodeList = new ArrayList<>();
		//????????????????????????????????????????????????????????????
		List<BookStorageItemBarCode> updateItemBarCodeList = new ArrayList<>();
		//1.??????
		if(null == item.getId()){
			item.remove("id");
			addItemBarCodeNum = item.getBookNum();
			//???????????????????????????????????????????????????
			//????????????
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
			//??????????????????
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
			//???????????????????????????book????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			//????????????
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

			//????????????????????????
			BookStorageItem itemDb = this.itemService.queryById(CurrentUser.getSchoolCode(), item.getId());
			item.setCatalogName(catalogName);
			item.setCreateTime(itemDb.getCreateTime());
			item.setCreateUserCode(itemDb.getCreateUserCode());
			item.setCreateUserName(itemDb.getCreateUserName());
			item.setDel(false);
			item.setSchoolCode(CurrentUser.getSchoolCode());
			item.setUpdateTime(new Date());
			item.setUpdateUserCode(CurrentUser.getUserCode());

			//?????????????????????
			updateItemBarCodeList = this.itemBarCodeService.queryByItemId(CurrentUser.getSchoolCode(), item.getId());
			//???????????????
			int deleteNum = updateItemBarCodeList.size() - item.getBookNum();
			addItemBarCodeNum = item.getBookNum() - updateItemBarCodeList.size();
			for(int i = 0; i < deleteNum; i++){
				updateItemBarCodeList.get(i).setDel(true);
			}
			//???????????????????????????
			updateItemBarCodeList.forEach(b -> {
				b.setPrice(item.getPrice());
				b.setCheckNo(checkNo);
			});
		}

		//??????????????????
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
		//??????????????????????????????
		if(addItemBarCodeNum > 0){
			String index = barCodeService.getBarCodeIndex(CurrentUser.getSchoolCode(), DateKit.toStr(new Date(), "yyMMdd"));
			String[] barCodes = BookStorageItemBarCodeKit.generateBarCode(CurrentUser.getSchoolCode(), addItemBarCodeNum, Integer.parseInt(index));
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
				//???????????????
				barCode.setBarCode(barCodes[i]);
				barCode.setCheckNo(checkNo);

				addItemBarCodeList.add(barCode);
			}
		}


		//????????????
		Book finalBook = book;
		boolean finalUpdateBook = updateBook;
		List<BookStorageItemBarCode> finalUpdateItemBarCodeList = updateItemBarCodeList;
		Db.tx(() -> {
			//???????????????????????????????????????????????????????????????????????????
			if(null == item.getId()){
				this.itemService.save(item);
			}else{
				this.itemService.update(item);
				//???????????????????????????
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

			//??????????????????????????????
			List<BookBarCode> bookBarCodeList = new ArrayList<>();
			//??????????????????????????????????????????????????????????????????????????????
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
			//???????????????????????????????????????????????????????????????
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
	 * ????????????id???????????????????????????????????????
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public JSONObject pageItemInfo(Long bookStorageId, int pageNumber, int pageSize) {
		//??????????????????
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
	 * ??????????????????
	 * @param id
	 */
	public void deleteItem(Long id){
		BookStorageItem s = this.itemService.queryById(CurrentUser.getSchoolCode(), id);
		if(null == s){
			throw new ErrorMsg("????????????");
		}
		//???????????????????????????????????????????????????
		if(StorageStatusEnum.GOING.getStatus() != s.getInt("status")){
			throw new ErrorMsg("?????????????????????????????????");
		}
		//????????????????????????book????????????????????????????????????????????????????????????
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
	 * ????????????????????????????????????
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
	 * ????????????id???????????????????????????
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
	 * ???????????????
	 * @param bookStorageId
	 * @param barCode
	 */
	public void confirm(Long bookStorageId, String barCode){
		List<BookStorageItemBarCode> codeList = this.itemBarCodeService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		List<BookBarCode> barCodeList = this.barCodeService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		if(null == codeList || codeList.isEmpty()){
			throw new ErrorMsg("??????????????????????????????????????????????????????");
		}
		if(codeList.size() > 1){
			throw new ErrorMsg("?????????????????????????????????????????????????????????????????????");
		}
		if(StorageStatusEnum.GOING.getStatus() != codeList.get(0).getInt("storage_status")){
			throw new ErrorMsg("????????????????????????????????????????????????");
		}
		if(ItemBarCodeStatusEnum.CONFIRM.getStatus() == codeList.get(0).getStatus().intValue()){
			throw new ErrorMsg("???????????????????????????");
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
	 * ????????????id???????????????????????????????????????
	 * @param bookStorageId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public JSONObject queryConfirmByStorageId(Long bookStorageId, int pageNumber, int pageSize){
		JSONObject json = new JSONObject();
		//??????????????????
		Page<BookStorageItem> page = this.itemService.queryConfirmByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				ItemBarCodeStatusEnum.CONFIRM.getStatus(), pageNumber, pageSize);
		JSONArray array = new JSONArray();
		page.getList().forEach(i -> {
			i.put("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
		});
		//??????????????????
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
	 * ???????????????????????????
	 * @param bookStorageId
	 * @param barCode
	 * @return
	 */
	public BookStorageItem queryItemByBarCode(Long bookStorageId, String barCode){
		List<BookStorageItem> itemList = this.itemService.queryByBarCode(CurrentUser.getSchoolCode(), bookStorageId, barCode);
		if(CollectionUtils.isEmpty(itemList)){
			throw new ErrorMsg("??????????????????????????????");
		}
		if(itemList.size() > 1){
			throw new ErrorMsg("?????????????????????????????????????????????????????????");
		}
		itemList.forEach(i -> {
			i.set("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
		});
		return itemList.get(0);
	}

	/**
	 * ????????????id??????????????????????????????
	 * @param bookStorageId
	 * @return
	 */
	public JSONObject getCountByStorageId(Long bookStorageId){
		JSONObject json = new JSONObject();
		//??????????????????
		Record totalRecord = this.itemBarCodeService.statisticsByStorageId(CurrentUser.getSchoolCode(), bookStorageId,
				ItemBarCodeStatusEnum.CONFIRM.getStatus(), ItemBarCodeStatusEnum.UNCONFIRM.getStatus());
		json.put("count", totalRecord);
		return json;
	}

	/**
	 * ????????????????????????????????????
	 * @param name
	 * @param beginTime
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

	public SXSSFWorkbook createExcelItemByName(String name, String beginTime, String endTime, String keyword) {

		JSONObject json = new JSONObject();
		String tableTitle = "????????????";
		Map<String, String> headMap = new LinkedHashMap<>();
		headMap.put("??????", "bar_code");
		headMap.put("??????", "book_name");
		headMap.put("??????", "author");
		headMap.put("?????????", "publisher");
		headMap.put("????????????/???", "price");
		headMap.put("????????????", "name");
		headMap.put("????????????", "create_time");
		headMap.put("?????????", "create_user_name");

		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("sheet1");
		SXSSFRow row = sheet.createRow(0);
		SXSSFCell cell;
		String key;

		CellStyle cellStyle = wb.createCellStyle();
		Font cellFont = wb.createFont();
		cellFont.setFontName("??????");
		cellFont.setFontHeightInPoints((short)10);
		cellStyle.setFont(cellFont);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		int rowIndex = 0;
		//?????????
		cell = row.createCell(rowIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(tableTitle);

		//?????????
		row = sheet.createRow(rowIndex++);
		//????????????
		//??????key???
		List<String> exportableKeyList = new ArrayList<String>();
		//?????????
		int firstCellIndex = 0;
		cell = row.createCell(firstCellIndex++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("??????");
		exportableKeyList.add("seq");
		//????????????
		int h = 0;
		for (Map.Entry<String, String > entry: headMap.entrySet() ) {

			sheet.setDefaultColumnStyle(h + firstCellIndex, cellStyle);
			cell = row.createCell(h + firstCellIndex);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(entry.getKey());
			exportableKeyList.add(entry.getValue());
			h++;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int pageNumber = 1;
		int pageSize = 500;
		Page<Record> page= this.itemBarCodeService.getItemByName(CurrentUser.getSchoolCode(), name, beginTime, endTime, keyword, pageNumber, pageSize);
		int totalRow = page.getTotalRow();
		int seqIndex = 1;
		while(page.getTotalPage() >= pageNumber){
			List<Record> list = page.getList();
			if(null!= list && !list.isEmpty()){
				for(int i = 0, len = list.size(); i < len; i++){
					Record r = list.get(i);
					row = sheet.createRow(rowIndex++);
					for ( int j = 0, size = exportableKeyList.size(); j < size; j++ ) {
						cell = row.createCell(j);
						cell.setCellStyle(cellStyle);
						key = exportableKeyList.get(j);
						switch ( key ) {
							case "seq":
								cell.setCellValue(seqIndex++);
								break;
							case "price":
								cell.setCellValue((double)r.getInt(key)/100);
								break;
							case "create_time":
								cell.setCellValue(sdf.format(r.getDate(key)));
								break;
							default:
								cell.setCellValue(r.getStr(key));
						}
					}

				}
			}
			pageNumber = pageNumber + 1;
			page = this.itemBarCodeService.getItemByName(CurrentUser.getSchoolCode(), name, beginTime, endTime, keyword, pageNumber, pageSize);
		}
		return wb;
	}

	/**
	 * ????????????????????????????????????
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getItemByNameCnt(String name, String beginTime, String endTime, String keyword) {
		Record record = this.itemBarCodeService.getItemByNameCnt(CurrentUser.getSchoolCode(), name, beginTime, endTime, keyword);
		return record.getStr("total_cnt");
	}

	/**
	 * ????????????????????????????????????
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @param keyword
	 * @return
	 */
	public String getItemByNameAmount(String name, String beginTime, String endTime, String keyword) {
		Record record = this.itemBarCodeService.getItemByNameAmount(CurrentUser.getSchoolCode(), name, beginTime, endTime, keyword);
		return record.getStr("total_amount");
	}

}