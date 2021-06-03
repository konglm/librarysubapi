package com.school.library.bookstorage;


import com.school.library.kit.BaseAddDelEditValidator;

/**
 * 编辑删除保存权限的校验器
 * @author jinshiye
 *
 */
public class DelEditValidator extends BaseAddDelEditValidator<BookStorageService> {

    public static final BookStorageService me = new BookStorageService();

    public DelEditValidator() {
        super(me);
    }

}