package com.school.library.book;

import com.jfnice.ext.IdMap;
import com.jfnice.model.Book;;

public class BookIdMap extends IdMap<Book, BookService> {

	public static final BookIdMap me = new BookIdMap();
	
}