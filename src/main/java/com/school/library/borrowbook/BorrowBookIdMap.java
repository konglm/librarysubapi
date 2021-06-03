package com.school.library.borrowbook;

import com.jfnice.ext.IdMap;
import com.jfnice.model.BorrowBook;;

public class BorrowBookIdMap extends IdMap<BorrowBook, BorrowBookService> {

	public static final BorrowBookIdMap me = new BorrowBookIdMap();
	
}