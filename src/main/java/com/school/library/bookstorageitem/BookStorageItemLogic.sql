#namespace("BookStorageItemLogic")
    #sql("queryById")
	    select a.*, b.status from book_storage_item a left join book_storage b on a.book_storage_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code) and a.id = #para(id)
	#end

	#sql("queryConfirmByStorageId")
	    select a.*, (select count(1) from book_storage_item_bar_code c where c.del = 0 and c.status = #(confirm_status) and
	        c.book_storage_item_id = a.id) as confirm_count
	    from book_storage_item a
	        where a.del = 0  and a.school_code = #para(school_code) and a.book_storage_id = #para(book_storage_id)
	        and exists (select 1 from book_storage_item_bar_code b where b.del = 0 and b.book_storage_item_id = a.id
	                and b.status = #para(confirm_status))
	        order by a.id
	#end

	#sql("queryBarCodeByStorageId")
	    select a.id, a.book_name, a.publisher, a.publish_date, a.author,
	    bar_codes = ( STUFF(( SELECT  ',' + b.bar_code
                         FROM book_storage_item_bar_code b
                         WHERE b.del = 0 and b.school_code = a.school_code
                            and b.book_storage_id = a.book_storage_id and b.book_storage_item_id = a.id
                         FOR
                         XML PATH('')
                         ), 1, 1, '')),
        check_nos = ( STUFF(( SELECT  ',' + b.check_no
                         FROM book_storage_item_bar_code b
                         WHERE b.del = 0 and b.school_code = a.school_code
                            and b.book_storage_id = a.book_storage_id and b.book_storage_item_id = a.id
                         FOR
                         XML PATH('')
                         ), 1, 1, ''))
            from book_storage_item a
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.book_storage_id = #para(book_storage_id)
	        order by a.id
	#end

	#sql("queryByStorageId")
	    select a.* from book_storage_item a
	        where a.del = 0  and a.school_code = #para(school_code) and a.book_storage_id = #para(book_storage_id)
	        order by a.id desc
	#end

	#sql("queryByBarCode")
	    select a.*, b.bar_code, b.check_no from book_storage_item a left join book_storage_item_bar_code b on a.id = b.book_storage_item_id
	        where a.del = 0 and b.del = 0 and a.school_code = #para(school_code) and b.bar_code = #para(bar_code)
	        and a.book_storage_id = #para(book_storage_id)
	#end

#end