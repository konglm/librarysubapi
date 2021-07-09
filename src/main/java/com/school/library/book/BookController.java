package com.school.library.book;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookBarCode;
import com.jfnice.model.Catalog;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.Book;
import com.school.api.model.BookList;
import com.school.library.bookbarcode.BookBarCodeLogic;
import com.school.library.bookstorageitembarcode.BookStorageItemBarCodeKit;
import com.school.library.catalog.CatalogLogic;
import com.school.library.catalog.CatalogService;
import com.school.library.constants.SysConstants;
import com.school.library.kit.CommonKit;
import com.school.library.search.SearchLogic;
import org.apache.commons.collections4.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Before(BookValidator.class)
public class BookController extends JFniceBaseController {
	

	@Inject
	private BookLogic logic;

	@Inject
	private BookBarCodeLogic bookBarCodeLogic;

	@Inject
	private CatalogService catalogService;

	@Inject
	private SearchLogic searchLogic;

	@Inject
	private BookService service;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<Book> page = logic.queryPage(condPara);

		if (CollectionUtils.isNotEmpty(page.getList())) {
			String res = PtApi.getPermissionByPositionList(new OpCodeEnum[]{OpCodeEnum.EDIT, OpCodeEnum.DELETE});
			page.getList().forEach(r -> {
				r.put("enable_edit", "1".equals(res.split(",")[0]));
				r.put("enable_delete", "1".equals(res.split(",")[1]));
			});
		}

		ok(page);
	}

	public void index() {
		ok(logic.queryList());
	}

	public void getById(@Para(value = "id", defaultValue = "0") long id) {
		ok(service.queryById(id, "*"));
	}

	@JsyPermissions(OpCodeEnum.ADD)
	@Before(TxPost.class)
	public void add() {
		Book book = getModel(Book.class, "", true);
		service.save(book);
		ok("保存成功！");
	}

	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		Book book = getModel(Book.class, "", true);
		service.update(book);
		ok("修改成功！");
	}

	@JsyPermissions(OpCodeEnum.DELETE)
	@Before(TxPost.class)
	public void delete(@Para(value = "id", defaultValue = "0") long id) {
		service.deleteById(id, false);
		ok("删除成功！");
	}

	@Before(Tx.class)
	public void sort() {
		Map<Long, Long> map = JSON.parseObject(getPara("sorts"), new TypeReference<Map<Long, Long>>() {
		});
		service.sort(map);
		ok("排序成功！");
	}

	/**
	 * 图书检索
	 */
	@Before(TxPost.class)
	public void booksearch(@Para("catalog_id") String catalogId,
						   @Para("keywords") String keywords,
                           @Para("order") String order,
						   @Para("unit_code") String unitCode,
						   @Para(value = "page_number", defaultValue = "1") int pageNumber,
						   @Para(value = "page_size", defaultValue = "10") int pageSize){
		Page<Record> recordPage = logic.bookSearch(keywords, catalogId, order,unitCode, pageNumber, pageSize);
		ok("查询成功",recordPage);
	}

	/**
	 * 前端图书检索
	 */
	@Before(TxPost.class)
	public void stuBooksearch(@Para("catalog_id") String catalogId,
						   @Para("keywords") String keywords,
						   @Para("order") String order,
						   @Para("unit_code") String unitCode,
						   @Para(value = "page_number", defaultValue = "1") int pageNumber,
						   @Para(value = "page_size", defaultValue = "10") int pageSize){
		if(!keywords.equals("")){
			searchLogic.recordSearch(keywords);
		}
		Page<Record> recordPage = logic.bookSearch(keywords, catalogId, order,unitCode, pageNumber, pageSize);
		ok("查询成功",recordPage);
	}

	/**
	 * 猜你喜欢
	 */
	public List<Record> areYouLike(){
		return null;
	}

	/**
	 * 根据书籍id查询相关信息
	 */
	@Before(TxPost.class)
	public void findByBook(@Para(value = "book_id", defaultValue = "0") long id,
						   @Para("unit_code") String unitCode) throws ParseException {
		Kv byBook = logic.findByBook(id, unitCode);
		ok("查询成功",byBook);
	}

	/**
	 * 修改书本信息
	 */
	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void editByBook(@Para(value = "id", defaultValue = "0") long id,
						   @Para("unit_code") String unitCode,
						   @Para("book_name") String bookName,
						   @Para("author") String author,
						   @Para("publisher") String publisher,
						   @Para("publish_date") String publishDate,
						   @Para("price") Long price,
						   @Para("catalog_id") String catalog_id) throws ParseException {
		String data = bookName + author + publisher +
				DateKit.toStr(CommonKit.parseDate(publishDate, "yyyy-MM"));
		String bookOrder = CommonKit.MD5(data);
		Catalog catalog = catalogService.queryById(unitCode, Long.parseLong(catalog_id));
		if(catalog == null ){
			throw new ErrorMsg("图书目录不存在,请重新选择其他目录!");
		}
		String CheckNo = BookStorageItemBarCodeKit.generateCheckNo(unitCode, catalog.getCatalogNo(), bookOrder);
		Book book = getModel(Book.class, "", true);
		book.setCheckNo(CheckNo);
		book.setCatalogName(catalog.getCatalogName());
		book.setPublishDate(CommonKit.parseDate(publishDate, "yyyy-MM"));
		service.update(book);
		bookBarCodeLogic.updateCheckNo(id,CheckNo,price);
		ok("修改成功");

	}

	/**
	 * 通过编号删除图书
	 */
	@JsyPermissions(OpCodeEnum.DELETE)
	@Before(TxPost.class)
	public void deleteByBarcode(@Para("unit_code") String unitCode,
								@Para(value = "bar_code") String barCode){
		bookBarCodeLogic.deleteByBarCode(barCode,unitCode);
		ok("删除成功");
	}

	/**
	 * 查询在馆图书
	 */
	public void getBooksIn(){
		int catalogId = getInt("catalog_id");
		String beginTime = getPara("begin_time");
		String endTime = getPara("end_time");
		String keyword = getPara("keyword");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		JSONObject data = new JSONObject();
		data.put("total_cnt", this.logic.getBooksInCnt(catalogId, beginTime, endTime, keyword));
		data.put("total_amount", this.logic.getBooksInAmount(catalogId, beginTime, endTime, keyword));
		data.put("list", this.logic.getBooksIn(catalogId, beginTime, endTime, keyword, pageNumber, pageSize));
		ok("查询书籍成功",data);

	}

	/**
	 * 查询外借图书
	 */
	public void getBooksBorrow(){
		int catalogId = getInt("catalog_id");
		int isOverDay= getInt("is_over_day");
		String beginTime = getPara("begin_time");
		String endTime = getPara("end_time");
		String keyword = getPara("keyword");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		JSONObject data = new JSONObject();
		data.put("total_cnt", this.logic.getBooksBorrowCnt(catalogId, isOverDay, beginTime, endTime, keyword));
		data.put("total_amount", this.logic.getBooksBorrowAmount(catalogId, isOverDay, beginTime, endTime, keyword));
		data.put("list", this.logic.getBooksBorrow(catalogId, isOverDay, beginTime, endTime, keyword, pageNumber, pageSize));
		ok("查询书籍成功",data);

	}

	/**
	 * 查询图书详情
	 */
	public void getBookInfoByBar(){
		String barCode = getPara("bar_code");
		int pageNumber = getParaToInt("page_number", SysConstants.DEFAULT_PAGE_NUMBER);
		int pageSize = getParaToInt("page_size", SysConstants.DEFAULT_PAGE_SIZE);
		JSONObject bookinfo = this.logic.getBookInfoByBar(barCode, pageNumber, pageSize);
		if(bookinfo == null){
			throw new ErrorMsg("未找到该图书!");
		}
		ok("查询书籍成功",bookinfo);
	}



}