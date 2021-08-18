package com.school.library.bookdamaged;

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
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.ext.ExcelExport;
import com.jfnice.interceptor.TxPost;
import com.jfnice.model.BookBarCode;
import com.jfnice.model.BookDamaged;
import com.jfnice.model.BorrowBook;
import com.school.api.gx.PtApi;
import com.school.library.bookbarcode.BookBarCodeLogic;
import com.school.library.bookinventory.BookInventoryLogic;
import com.school.library.bookdamaged.BookDamagedValidator;
import com.school.library.bookdamaged.DelEditValidator;
import com.school.library.borrowbook.BorrowBookService;
import com.school.library.kit.CommonKit;
import com.school.library.kit.JsyAddDelEdit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Date;
import java.util.Map;

@Before({BookDamagedValidator.class, DelEditValidator.class})
public class BookDamagedController extends JFniceBaseController {
	
	private final static String ACCESS = "library:BookDamaged:index";
	private final static String ADD = "library:Operation:add";
	private final static String EDIT = "library:Operation:edit";
	private final static String DELETE = "library:Operation:delete";

	@Inject
	private BookDamagedLogic logic;
	@Inject
	private BookDamagedService service;

	@Inject
	private BorrowBookService borrowService;

	@Inject
	private BookBarCodeLogic bookBarCodeLogic;
    @Inject
    private BookInventoryLogic bookInventoryLogic;

	@JsyPermissions(OpCodeEnum.INDEX)
	public void page() {
		CondPara condPara = JsonKit.parse(getRawData(), CondPara.class);
		Page<BookDamaged> page = logic.queryPage(condPara);

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
		BookDamaged bookDamaged = getModel(BookDamaged.class, "", true);
		service.save(bookDamaged);
		ok("保存成功！");
	}

	@JsyPermissions(OpCodeEnum.EDIT)
	@Before(TxPost.class)
	public void edit() {
		BookDamaged bookDamaged = getModel(BookDamaged.class, "", true);
		service.update(bookDamaged);
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
	 * 登记图书是否损坏
	 */
	@Before(TxPost.class)
	public void  recordDamage(@Para("unit_code") String unitCode,
							  @Para(value = "bar_code") String barCode,
							  @Para(value = "book_status") int bookStatus,
							  @Para(value = "deductions") double deductions,
							  @Para(value = "borrower_name")String borrowerName,
							  @Para(value = "explain")String explain,
							  @Para(value = "book_name")String bookName,
							  @Para(value = "author")String author,
							  @Para(value = "borrow_id") int borrowId){
        boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
        if(ispan){
            throw new ErrorMsg("学校正在盘点书籍,不可进行登记!");
        }
		Integer cost = CommonKit.formatMoneyToFen(deductions);
		BookDamaged bookDamaged = getModel(BookDamaged.class, "", true);
		logic.recordDamage(barCode, unitCode,borrowId,cost,bookStatus,bookDamaged);
		ok("登记成功");

	}

	/**
	 * 修复图书
	 */
//	@JsyPermissions(OpCodeEnum.EDIT)
//	@JsyAddDelEdit(requirePrimary = true)
	@Before(TxPost.class)
	public void repairBook(@Para(value = "id", defaultValue = "0") long id,
						   @Para(value = "last_status")int lastStatus,
						   @Para(value = "bar_code") String barCode,
						   @Para("unit_code") String unitCode){
        boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
        if(ispan){
            throw new ErrorMsg("学校正在盘点书籍,不可进行修复!");
        }
		logic.repairBook(barCode,unitCode,lastStatus,id);
		ok("修复成功");
	}

	/**
	 * 查询列表
	 */
	@Before(TxPost.class)
	public void damagedList(@Para("unit_code") String unitCode,
							@Para("repair_type") String repairType,
							@Para("book_status") String bookStatus,
							@Para("keywords") String keywords,
							@Para(value = "page_number", defaultValue = "1") int pageNumber,
							@Para(value = "page_size", defaultValue = "10") int pageSize){

		JSONObject data = new JSONObject();
		String totalCnt = logic.damagedTotalCnt(unitCode, keywords, repairType, bookStatus);
		data.put("total_cnt", totalCnt);
		String totalAmount = logic.damagedTotalAmount(unitCode, keywords, repairType, bookStatus);
		data.put("total_amount", totalAmount);
		Page<BookDamaged> page = logic.damagedList(unitCode, pageNumber, pageSize,keywords,repairType,bookStatus);
		data.put("page_number", page.getPageNumber());
		data.put("page_size", page.getPageSize());
		data.put("total_page", page.getTotalPage());
		data.put("total_row", page.getTotalRow());
		data.put("list", page.getList());
		ok("查询成功",data);
	}

	@JsyPermissions(OpCodeEnum.INDEX)
	public void excelDamagedList(@Para("unit_code") String unitCode,
								 @Para("repair_type") String repairType,
								 @Para("book_status") String bookStatus,
								 @Para("keywords") String keywords){
		SXSSFWorkbook wb = this.logic.createExcelDamagedList(unitCode, keywords,repairType,bookStatus);
		render(new ExcelExport(wb, "问题图书"));
	}

	/**
	 * 损毁详情
	 */
	@Before(TxPost.class)
	public void damagedDetail(@Para(value = "id", defaultValue = "0") long id){
		Record record = logic.damagedDetail(id);
        String prices = CommonKit.formatMoney(Integer.parseInt(record.getStr("price")));
        String deductions =CommonKit.formatMoney(Integer.parseInt(record.getStr("deductions")));
		record.set("deductions",deductions);
		record.set("price",prices);
		ok("查询成功",record);
	}

	/**
	 * 押金扣除审核列表
	 */
    @Before(TxPost.class)
	public void checkList(@Para("judge") String judge,
						  @Para("book_status") String bookStatus,
						  @Para("start_time") String startTime,
						  @Para("end_time") String endTime,
						  @Para("unit_code") String unitCode,
						  @Para(value = "page_number", defaultValue = "1") int pageNumber,
						  @Para(value = "page_size", defaultValue = "5") int pageSize){
        Page<Record> recordPage = logic.checkList(judge, bookStatus, startTime, endTime,unitCode, pageNumber, pageSize);
        for (Record record:recordPage.getList()){
            String deduction = CommonKit.formatMoney(Integer.parseInt(record.getStr("deductions")));
			String price = CommonKit.formatMoney(Integer.parseInt(record.getStr("price")));
			record.set("price",price);
            record.set("deductions",deduction);
        }
        ok("查询列表成功",recordPage);
    }

    /**
     * 金额修改
     */
//	@JsyPermissions(OpCodeEnum.EDIT)
    @Before(TxPost.class)
    public void changeDeduction(@Para(value = "new_deduction") double newDeduction,
                                @Para(value = "borrow_id", defaultValue = "0") long borrowId,
                                @Para(value = "id", defaultValue = "0") long id){
        boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
        if(ispan){
            throw new ErrorMsg("学校正在盘点书籍,不可进行修改!");
        }
		if(newDeduction<0){
			throw new ErrorMsg("金额不能为负数,请重新输入");
		}
		Integer newCost = CommonKit.formatMoneyToFen(newDeduction);
		logic.changeDeduction(newCost,borrowId,id);
        ok("金额修改成功");
    }

	/**
	 * 破损赔偿审核
	 */
	@Before(TxPost.class)
	public void judge(@Para("unit_code") String unitCode,
					  @Para("user_type") String user_type,
					  @Para("user_code") String user_code,
					  @Para("stu_code") String stu_code,
			   		  @Para(value = "id", defaultValue = "0") long id,
					  @Para("judge") int judge,
					  @Para(value = "deduction") double deduction){
        boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
        if(ispan){
            throw new ErrorMsg("学校正在盘点书籍,不可进行审核!");
        }
		if(deduction<0){
			throw new ErrorMsg("金额不能为负数,请重新输入");
		}
		Integer cost = CommonKit.formatMoneyToFen(deduction);
		logic.judge(id,judge,unitCode,user_type,user_code,stu_code,cost);
		ok("审核成功");
	}

	@Before(TxPost.class)
	public void  writeOffBook(@Para("unit_code") String unitCode,
							  @Para(value = "bar_code") String barCode,
							  @Para(value = "deductions") double deductions,
							  @Para(value = "borrower_name")String borrowerName,
							  @Para(value = "explain")String explain,
							  @Para(value = "book_name")String bookName,
							  @Para(value = "author")String author,
							  @Para(value = "borrow_id") int borrowId){
		boolean ispan = bookInventoryLogic.hasGoingInventory(CurrentUser.getSchoolCode());
		if(ispan){
			throw new ErrorMsg("学校正在盘点书籍,不可进行注销!");
		}
		BookDamaged bookDamaged = getModel(BookDamaged.class, "", true);
		logic.writeOffBook(unitCode, bookDamaged);
		ok("注销成功");

	}

}