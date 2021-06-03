#namespace("BookInventoryItemLogic")
    #sql("insertFromBarCode")
	    insert into book_inventory_item (del, create_time, create_user_code, create_user_name, update_time,
	        update_user_code, school_code, book_inventory_id, status, catalog_id, catalog_no, catalog_name,
	        book_name, author, publisher, publish_date, bar_code, check_no, book_status, price, book_img_url)
            select 0, #para(now_time), #para(user_code), #para(create_user_name), #para(now_time),
                #para(user_code), #para(school_code), #para(book_inventory_id), #para(status), b.catalog_id, b.catalog_no, b.catalog_name,
                b.book_name, b.author, b.publisher, b.publish_date, a.bar_code, a.check_no, a.status, a.price, b.book_img_url
            from book_bar_code a left join book b on a.book_id = b.id
            where a.del = 0 and a.school_code = #para(school_code) and b.del = 0
            and not exists
            (select 1 from borrow_book c where c.del = 0 and a.school_code = c.school_code and c.book_id = a.book_id
                and a.bar_code = c.bar_code and c.return_status = #para(un_return_status))
            #if(not_include_status)
                and a.status not in (#(not_include_status))
            #end
	#end

	#sql("queryByBarCode")
	   select a.*, b.status as inventory_status from book_inventory_item a left join book_inventory b
	        on b.del = 0 and b.id = a.book_inventory_id
            where a.del = 0 and b.del = 0 and a.school_code = #para(school_code) and a.book_inventory_id = #para(book_inventory_id)
            and a.bar_code = #para(bar_code)
	#end

	#sql("statisticsByStatus")
	   select status, count(1) as status_count from book_inventory_item a
            where a.del = 0 and a.school_code = #para(school_code) and a.book_inventory_id = #para(book_inventory_id)
            group by status
	#end

	#sql("queryConfirmList")
	    select a.* from book_inventory_item a
	        where a.del = 0 and a.school_code = #para(school_code) and a.book_inventory_id = #para(book_inventory_id)
	        and a.status = #para(status)
	        order by inventory_time desc
	#end

	#sql("queryUnConfirmList")
	    select a.* from book_inventory_item a
	        where a.del = 0 and a.school_code = #para(school_code) and a.book_inventory_id = #para(book_inventory_id)
	        and a.status = #para(status)
	        order by id desc
	#end

#end