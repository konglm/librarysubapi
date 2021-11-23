#namespace("BookBarCodeLogic")
    #sql("deleteByItemId")
	    delete from book_bar_code where del = 0 and school_code = #para(school_code)
	        and book_storage_item_id = #para(book_storage_item_id)
	#end

	#sql("logicDeleteByItemId")
	    update book_bar_code set del = 1, update_user_code = #para(update_user_code), update_time = #para(update_time)
	        where del = 0 and school_code = #para(school_code)
	        and book_storage_item_id = #para(book_storage_item_id)
	#end

	#sql("queryByStorageId")
	    select a.*, b.book_name from book_bar_code a left join book_storage_item b on a.book_storage_item_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.book_storage_id = #para(book_storage_id)
	        order by a.book_id, a.id
	#end

	#sql("queryByBarCode")
	    select a.*, b.status as storage_status from book_bar_code a left join book_storage b on a.book_storage_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.book_storage_id = #para(book_storage_id)
	        and a.bar_code = #para(bar_code)
	#end

	#sql("statisticsByStorageId")
	    select isnull( sum (case when a.status = #para(unconfirm_status) then 1
	        else 0 end), 0) as unconfirm_count,
	     isnull( sum (case when a.status = #para(confirm_status) then 1
	        else 0 end), 0) as confirm_count
	    from book_bar_code a left join book_storage_item b on  a.book_storage_item_id = b.id
	        left join book_storage c on  a.book_storage_id = c.id
	        where a.del = 0 and b.del = 0 and c.del = 0 and
	        a.school_code = #para(school_code) and a.book_storage_id = #para(book_storage_id)
	#end

	#sql("statisticsByCatalogNo")
	    select b.catalog_no, b.catalog_name, count(1) as book_count
	    from book_bar_code a left join book b on a.book_id = b.id
	        where a.del = 0 and b.del = 0 and a.school_code = #para(school_code)
	        and a.status = #para(confirm_status)
	        group by b.catalog_no, b.catalog_name
	#end

	#sql("updateCheckNo")
	    update book_bar_code set check_no = #para(check_no),price = #(price) where status <> '0' and book_id = #(book_id)
	#end

	#sql("findByBarcode")
	    select * from book_bar_code where del = 0 and school_code = #para(school_code) and bar_code = #para(bar_code)
	#end

	#sql("deleteByBarcode")
	    update book_bar_code set del = 1 where school_code = #para(school_code) and bar_code = #para(bar_code)
	#end

	#sql("writeoffByBarcode")
        update book_bar_code set status = 6, del_reason = #para(del_reason)
        where school_code = #para(school_code) and bar_code = #para(bar_code)
    #end

    #sql("storageBarCode")
	    update book_bar_code set status = #para(storage_status), update_user_code = #para(update_user_code),
	        update_time = #para(update_time)
	        where del = 0 and school_code = #para(school_code)
	        and book_storage_id = #para(book_storage_id) and status = #para(confirm_status)
	#end

	#sql("statisticsTotalCnt")
        select count(1) total_cnt
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and ((a.status = 1) or (a.status = 2))
    #end

    #sql("statisticsTotalAmount")
        select isnull(sum(price),0) total_amount
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and ((a.status = 1) or (a.status = 2))
    #end

    #sql("statisticsTotalIn")
        select count(1) total_cnt
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 1 and a.bar_code
        not in (select bar_code from borrow_book where del = 0 and school_code = #para(school_code) and return_status = 0)
    #end

    #sql("statisticsTotalOut")
        select count(1) total_cnt
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 1 and a.bar_code
        in (select bar_code from borrow_book where del = 0 and school_code = #para(school_code) and return_status = 0)
    #end

    #sql("statisticsTotalRepair")
        select count(1) total_cnt
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 2
    #end

    #sql("statisticsTotalDamage")
        select count(1) total_damage_cnt, isnull(sum(price),0) total_damage_amount
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 3
    #end

    #sql("statisticsTotalLose")
        select count(1) total_lose_cnt, isnull(sum(price),0) total_lose_amount
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 4
    #end

    #sql("statisticsTotalWriteOff")
        select count(1) total_write_off_cnt, isnull(sum(price),0) total_write_off_amount
        from book_bar_code a, book b
        where a.book_id = b.id and a.school_code = #para(school_code)
        and a.del = 0 and b.del = 0 and a.status = 6
    #end

#end