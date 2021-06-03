package com.school.library.bookinventory;

import com.jfnice.ext.IdMap;
import com.jfnice.model.BookInventory;;

public class BookInventoryIdMap extends IdMap<BookInventory, BookInventoryService> {

	public static final BookInventoryIdMap me = new BookInventoryIdMap();
	
}