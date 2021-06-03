package com.school.library.bookdamaged;


import com.school.library.bookstorage.BookStorageService;
import com.school.library.kit.BaseAddDelEditValidator;

/**
 * 编辑删除保存权限的校验器
 * @author jinshiye
 *
 */
public class DelEditValidator extends BaseAddDelEditValidator<BookDamagedService> {

    public static final BookDamagedService me = new BookDamagedService();

    public DelEditValidator() {
        super(me);
    }

}