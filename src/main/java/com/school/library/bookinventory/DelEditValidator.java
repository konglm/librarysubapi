package com.school.library.bookinventory;


import com.school.library.kit.BaseAddDelEditValidator;

/**
 * 编辑删除保存权限的校验器
 * @author jinshiye
 *
 */
public class DelEditValidator extends BaseAddDelEditValidator<BookInventoryService> {

    public static final BookInventoryService me = new BookInventoryService();

    public DelEditValidator() {
        super(me);
    }

}