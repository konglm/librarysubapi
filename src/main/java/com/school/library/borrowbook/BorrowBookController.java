package com.school.library.borrowbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.ext.ExcelExport;
import com.jfnice.model.Book;
import com.jfnice.model.BorrowSetting;
import com.school.api.gx.PtApi;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.ext.CondPara;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.BorrowBook;
import com.school.library.book.BookService;
import com.school.library.bookinventory.BookInventoryLogic;
import com.school.library.borrowsetting.BorrowSettingLogic;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Before(BorrowBookValidator.class)
public class BorrowBookController extends JFniceBaseController {
	
	private final static String ACCESS = "library:BorrowBook:index";
	private final static String ADD = "library:Operation:add";
	private final static String EDIT = "library:Operation:edit";
	private final static String DELETE = "library:Operation:delete";

	@Inject
	private BorrowBookLogic logic;
	@Inject
	private BorrowBookService service;
    @Inject
    private BookService bookService;
	@Inject
	private BorrowSettingLogic settingLogic;
	@Inject
	private BookInventoryLogic bookInventoryLogic;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<BorrowBook> page = logic.queryPage(condPara);

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
		BorrowBook borrowBook = getModel(BorrowBook.class, "", true);
		service.save(borrowBook);
		ok("保存成功！");
	}

	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		BorrowBook borrowBook = getModel(BorrowBook.class, "", true);
		service.update(borrowBook);
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
	 * 查询借阅人
	 */
	@Before(Tx.class)
	public void getUserInfoById(@Para(value = "stu_code", defaultValue = "0")  long stuCode,
								@Para(value = "user_code", defaultValue = "0") long userCode){
		Record userInfo = logic.getUserInfoById(stuCode,userCode);
		ok("查询个人成功",userInfo);
	}

	/**
	 * 查询图书信息
	 * @param schoolCode
	 * @param barCode
	 */
	@Before(Tx.class)
    public void getBookInfoByBar(@Para(value = "unit_code") String schoolCode,
                                 @Para(value = "bar_code") String barCode){
		boolean borrow = logic.isBorrow(barCode, schoolCode);
		if(borrow){
			throw new ErrorMsg("该书籍状态异常或已被借出,不可进行租借!");
		}
		Book bookinfo = bookService.queryByBarCode(schoolCode, barCode);
		if(bookinfo == null){
			throw new ErrorMsg("未找到该图书!");
		}
        ok("查询书籍成功",bookinfo);
    }

	/**
	 * 借出图书
	 */
	@Before(Tx.class)
	public  void borrowBook(@Para(value = "stu_code") long stuCode,
							@Para(value = "user_code") long userCode,
							@Para(value = "bar_code_list") String  barCodeList,
							@Para(value = "borrowed") int borrowed,
							@Para(value = "unit_code") String schoolCode){
		boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
		if(ispan){
			throw new ErrorMsg("学校正在盘点书籍,请在盘点完后再借出图书!");
		}
		logic.borrowBook(stuCode,userCode,barCodeList,borrowed,schoolCode);
        ok("借出成功");
	}

	/**
	 * 通过bar_code查询归还图书
	 */
	@Before(Tx.class)
	public void payBookByBarCode(@Para(value = "unit_code") String schoolCode,
								 @Para(value = "bar_code") String barCode) throws ParseException {
		Record borrowBook = logic.payBookByBarCode(schoolCode,barCode);
		ok("查询成功",borrowBook);
	}

	/**
	 * 通过bar_code查询需归还图书
	 */
	@Before(Tx.class)
	public void paybookList(@Para(value = "unit_code") String schoolCode,
							@Para(value = "user_type") String userType,
							@Para(value = "user_code") String user_code) throws ParseException {
		List<Record> records = logic.paybookList(schoolCode, userType, user_code);
		ok("查询成功",records);
	}

	/**
	 * 还书
	 */
	@Before(Tx.class)
	public void payBook(@Para(value = "user_type") String userType,
						@Para(value = "user_code") String userCode,
						@Para(value = "bar_code_list") String  barCodeList,
						@Para(value = "unit_code") String schoolCode){
		boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
		if(ispan){
			throw new ErrorMsg("学校正在盘点书籍,请在盘点完后再归还图书!");
		}
		logic.payBook(userType,userCode,barCodeList,schoolCode);
		ok("操作成功");
	}

	/**
	 * 押金扣除记录
	 */
	public void depositList(@Para("keywords") String keywords,
							@Para("start_time") String startTime,
							@Para("end_time") String endTime,
							@Para(value = "page_number", defaultValue = "1") int pageNumber,
							@Para(value = "page_size", defaultValue = "10") int pageSize){
        JSONObject data =  new JSONObject();
        String totalAmount = logic.getTotalDepositAmount(keywords, startTime, endTime);
        data.put("total_amount",  totalAmount);
        Page<Record> page = logic.depositList(keywords, startTime, endTime, pageNumber, pageSize);
		data.put("page_number", page.getPageNumber());
		data.put("page_size", page.getPageSize());
		data.put("total_page", page.getTotalPage());
		data.put("total_row", page.getTotalRow());
		data.put("list", page.getList());
        ok("查询成功",data);
	}

	/**
	 * 押金扣除记录
	 * @param keywords
	 * @param startTime
	 * @param endTime
	 */
	@JsyPermissions(OpCodeEnum.INDEX)
	public void excelDepositList(@Para("keywords") String keywords,
								 @Para("begin_time") String startTime,
								 @Para("end_time") String endTime){
		SXSSFWorkbook wb = this.logic.createExcelDepositList(keywords,startTime,endTime);
		render(new ExcelExport(wb, "押金扣除记录"));
	}

}