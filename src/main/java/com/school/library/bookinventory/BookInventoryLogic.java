package com.school.library.bookinventory;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.model.BookInventory;
import com.jfnice.model.BookInventoryItem;
import com.jfnice.model.BookStorage;
import com.school.api.gx.PtApi;
import com.school.library.bookbarcode.BookBarCodeStatusEnum;
import com.school.library.bookinventoryitem.BookInventoryItemService;
import com.school.library.bookinventoryitem.BookInventoryItemStatusEnum;
import com.school.library.bookstorage.BookStorageService;
import com.school.library.borrowbook.ReturnStatusEnum;
import com.school.library.constants.DictConstants;
import com.school.library.constants.RedisConstants;
import com.school.library.kit.CommonKit;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookInventoryLogic {

	@Inject
	private BookInventoryService service;
	@Inject
	private BookStorageService storageService;
	@Inject
	private BookInventoryItemService itemService;

	/**
	 * 分页查询首页记录
	 * @return
	 */
	public Page<BookInventory> queryIndexPage(CondPara condPara){
		condPara.put("school_code", CurrentUser.getSchoolCode());
		Page<BookInventory> page = this.service.queryIndexPage(condPara);
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
				r.put("status_text", DictKit.text(DictConstants.INVENTORY_STATUS_TAG, r.getStatus()));
			});
		}

		return page;
	}

	/**
	 * 判断有无正在盘点的任务
	 * @param schoolCode
	 * @return
	 */
	public boolean hasGoingInventory(String schoolCode){
		//查询redis有无正在盘点的任务
		String redisKey = RedisConstants.BOOK_INVENTORY_KEY_PREFIX + CurrentUser.getSchoolCode();
		Integer redisFlag = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, redisKey);
		if(null != redisFlag && redisFlag >= 1){
			return true;
		}
		//查询数据库有无正在盘点的记录
		List<BookInventory> inventoryList = this.service.queryByStatus(CurrentUser.getSchoolCode(), BookInventoryStatusEnum.GOING.getStatus());
		if(CollectionUtils.isNotEmpty(inventoryList)){
			return true;

		}
		return false;
	}

	/**
	 * 新建盘点
	 */
	public void add(){
		//查询redis、数据库有无正在入库的任务
		String storageRedisKey = RedisConstants.BOOK_STORAGE_KEY_PREFIX + CurrentUser.getSchoolCode();
		Integer storageRedisFlag = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, storageRedisKey);
		if(null != storageRedisFlag && storageRedisFlag >= 1){
			throw new ErrorMsg("存在尚未结束的入库任务，不能新建盘点任务");
		}
		List<BookStorage> storageList = this.storageService.queryByStatus(CurrentUser.getSchoolCode(), BookInventoryStatusEnum.GOING.getStatus());
		if(CollectionUtils.isNotEmpty(storageList)){
			throw new ErrorMsg("存在尚未结束的入库任务，不能新建盘点任务");
		}

		//查询有无正在盘点的记录
		boolean going = this.hasGoingInventory(CurrentUser.getSchoolCode());
		if(going){
			throw new ErrorMsg("存在尚未结束的盘点任务，不能新建盘点任务");
		}
		String redisKey = RedisConstants.BOOK_INVENTORY_KEY_PREFIX + CurrentUser.getSchoolCode();
		try{
			//设置redis值
			JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 1, RedisConstants.TIME_TO_LIVE_SECONDS);
			StringBuilder nameBuild = new StringBuilder();
			nameBuild.append(DateKit.toStr(new Date(), "yyyy年MM月dd日")).append("图书盘点");
			BookInventory inventory = new BookInventory();
			inventory.setName(nameBuild.toString());
			inventory.setCreateTime(new Date());
			inventory.setCreateUserCode(CurrentUser.getUserCode());
			inventory.setCreateUserName(CurrentUser.getUserName());
			inventory.setDel(false);
			inventory.setSchoolCode(CurrentUser.getSchoolCode());
			inventory.setStatus(BookInventoryStatusEnum.GOING.getStatus());
			inventory.setUpdateTime(new Date());
			inventory.setUpdateUserCode(CurrentUser.getUserCode());
			this.service.save(inventory);
			//同时插入图书盘点明细
			//不计入盘点的图书状态
			String notIncludeStatus = Arrays.asList(new Integer[]{
					BookBarCodeStatusEnum.UNCONFIRM.getStatus(),
					BookBarCodeStatusEnum.DAMAGE.getStatus(),
					BookBarCodeStatusEnum.LOSE.getStatus(),
					BookBarCodeStatusEnum.CONFIRM.getStatus()}
					).stream()
					.map(s -> s.toString())
					.collect(Collectors.joining(","));
			this.itemService.insertFromBarCode(new Date(),CurrentUser.getUserCode(), CurrentUser.getUserName(),
					CurrentUser.getSchoolCode(), inventory.getId(), BookInventoryItemStatusEnum.UN_CONFIRM.getStatus(),
					notIncludeStatus, ReturnStatusEnum.UN_RETURN.getStatus());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 0, RedisConstants.TIME_TO_LIVE_SECONDS);
		}
	}

	/**
	 * 逻辑删除
	 * @param id
	 * @return
	 */
	public boolean logicDelete(Long id){
		BookInventory inventory = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null != inventory){
			inventory.setUpdateUserCode(CurrentUser.getUserCode());
			inventory.setUpdateTime(new Date());
			inventory.setDel(true);
			return this.service.update(inventory);
		}
		return false;
	}

	/**
	 * 结束入库
	 * @param id
	 * @return
	 */
	public boolean end(Long id){
		BookInventory inventory = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null != inventory){
			inventory.setUpdateUserCode(CurrentUser.getUserCode());
			inventory.setUpdateTime(new Date());
			inventory.setStatus(BookInventoryStatusEnum.END.getStatus());
			return this.service.update(inventory);
		}
		return false;
	}

	/**
	 * 通过条形码查询盘点明细
	 * @param bookInventoryId
	 * @param barCode
	 * @return
	 */
	public BookInventoryItem queryItemByBarCode(Long bookInventoryId, String barCode){
		List<BookInventoryItem> itemList = this.itemService.queryByBarCode(CurrentUser.getSchoolCode(), bookInventoryId, barCode);
		if(CollectionUtils.isEmpty(itemList)){
			throw new ErrorMsg("此次盘点没有此条形码");
		}
		if(itemList.size() > 1){
			throw new ErrorMsg("此条形码重复，请重新入库更换新的条形码");
		}
		itemList.forEach(i -> {
			i.set("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			i.set("price", CommonKit.formatMoney(i.getPrice()));
			i.put("book_status_text", DictKit.text(DictConstants.BOOK_STATUS_TAG, i.getBookStatus()));
		});
		return itemList.get(0);
	}

	/**
	 * 扫码确认
	 * @param bookInventoryId
	 * @param barCode
	 * @return
	 */
	public JSONObject confirm(Long bookInventoryId, String barCode){
		List<BookInventoryItem> itemList = this.itemService.queryByBarCode(CurrentUser.getSchoolCode(), bookInventoryId, barCode);
		if(CollectionUtils.isEmpty(itemList)){
			throw new ErrorMsg("该条形码[" + barCode + "]不存在");
		}
		if(itemList.size() > 1){
			throw new ErrorMsg("该条形码重复，确认失败");
		}
		BookInventoryItem item = itemList.get(0);
		if(!Objects.equals(item.getInt("inventory_status"), BookInventoryStatusEnum.GOING.getStatus())){
			throw new ErrorMsg("该次盘点已结束或者被删除，不能再确认");
		}
		item.setUpdateTime(new Date());
		item.setUpdateUserCode(CurrentUser.getUserCode());
		item.setInventoryTime(new Date());
		item.setStatus(BookInventoryItemStatusEnum.CONFIRM.getStatus());
		this.itemService.update(item);
		JSONObject countJson = this.queryStatisticsCount(bookInventoryId);

		JSONObject data = new JSONObject();
		data.put("item", item);
		data.putAll(countJson);
		return data;
	}

	/**
	 * 查询统计数量
	 * @param bookInventoryId
	 * @return
	 */
	public JSONObject queryStatisticsCount(Long bookInventoryId){
		List<Record> recordList = this.itemService.statisticsByStatus(CurrentUser.getSchoolCode(), bookInventoryId);
		int totalCount = 0;
		int confirmCount = 0;
		int unConfirmCount = 0;
		if(CollectionUtils.isNotEmpty(recordList)){
			for (Record record : recordList) {
				if(record.getInt("status").intValue() == BookInventoryItemStatusEnum.CONFIRM.getStatus()){
					confirmCount = record.getInt("status_count");
				}else if(record.getInt("status").intValue() == BookInventoryItemStatusEnum.UN_CONFIRM.getStatus()){
					unConfirmCount = record.getInt("status_count");
				}
			}
		}

		totalCount = confirmCount + unConfirmCount;
		JSONObject data = new JSONObject();
		data.put("total_count", totalCount);
		data.put("confirm_count", confirmCount);
		data.put("un_confirm_count", unConfirmCount);
		return data;
	}

	/**
	 * 分页查询盘点明细
	 * @param bookInventoryId
	 * @param status 明细状态
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public JSONObject queryPageItem(Long bookInventoryId, int status, int pageNumber, int pageSize){
		Page<BookInventoryItem> page = null;
		if(BookInventoryItemStatusEnum.CONFIRM.getStatus() == status){
			page = this.itemService.pageConfirmList(CurrentUser.getSchoolCode(), bookInventoryId, status,
					pageNumber, pageSize);
			page.getList().forEach(i -> {
				i.put("inventory_time", DateKit.toStr(i.getInventoryTime(), "yyyy-MM-dd HH:mm"));
				i.put("price", CommonKit.formatMoney(i.getPrice()));
				i.put("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			});
		}else if(BookInventoryItemStatusEnum.UN_CONFIRM.getStatus() == status){
			page = this.itemService.pageUnConfirmList(CurrentUser.getSchoolCode(), bookInventoryId, status,
					pageNumber, pageSize);
			page.getList().forEach(i -> {
				i.put("price", CommonKit.formatMoney(i.getPrice()));
				i.put("publish_date", DateKit.toStr(i.getPublishDate(), "yyyy-MM"));
			});
		}

		JSONObject data = this.queryStatisticsCount(bookInventoryId);
		if(null != page){
			data.put("total_row", page.getTotalRow());
			data.put("total_page", page.getTotalPage());
			data.put("page_size", page.getPageSize());
			data.put("page_number", page.getPageNumber());
			data.put("list", page.getList());
		}
		return data;
	}


}