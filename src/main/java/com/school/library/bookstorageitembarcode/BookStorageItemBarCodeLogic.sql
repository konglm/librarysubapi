#namespace("BookStorageItemBarCodeLogic")
    #sql("deleteByItemId")
	    delete from book_storage_item_bar_code where del = 0 and school_code = #para(school_code)
	        and book_storage_item_id = #para(book_storage_item_id)
	#end

	#sql("logicDeleteByItemId")
	    update book_storage_item_bar_code set del = 1, update_user_code = #para(update_user_code), update_time = #para(update_time)
	        where del = 0 and school_code = #para(school_code)
	        and book_storage_item_id = #para(book_storage_item_id)
	#end

	#sql("queryByStorageId")
	    select a.*, b.book_name from book_storage_item_bar_code a left join book_storage_item b on a.book_storage_item_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.book_storage_id = #para(book_storage_id)
	        order by a.book_id, a.id
	#end

	#sql("queryByBarCode")
	    select a.*, b.status as storage_status from book_storage_item_bar_code a left join book_storage b on a.book_storage_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.book_storage_id = #para(book_storage_id)
	        and a.bar_code = #para(bar_code)
	#end

	#sql("statisticsByStorageId")
	    select isnull( sum (case when a.status = #para(unconfirm_status) then 1
	        else 0 end), 0) as unconfirm_count,
	     isnull( sum (case when a.status = #para(confirm_status) then 1
	        else 0 end), 0) as confirm_count
	    from book_storage_item_bar_code a left join book_storage_item b on  a.book_storage_item_id = b.id
	        left join book_storage c on  a.book_storage_id = c.id
	        where a.del = 0 and b.del = 0 and c.del = 0 and
	        a.school_code = #para(school_code) and a.book_storage_id = #para(book_storage_id)
	#end

	#sql("statisticsTotalByStorageId")
	    select isnull(count(1), 0) as total_count
	    from book_storage_item_bar_code a left join book_storage_item b on  a.book_storage_item_id = b.id
	        left join book_storage c on  a.book_storage_id = c.id
	        where a.del = 0 and b.del = 0 and c.del = 0 and
	        a.school_code = #para(school_code) and a.book_storage_id = #para(book_storage_id)
	#end

	#sql("statisticsByCatalogNo")
	    select b.catalog_no, b.catalog_name, count(1) as book_count
	    from book_storage_item_bar_code a left join book b on a.book_id = b.id
	        where a.del = 0 and b.del = 0 and a.school_code = #para(school_code)
	        group by b.catalog_no, b.catalog_name
	#end

	#sql("updateCheckNo")
	    update book_storage_item_bar_code set check_no = #para(check_no),price = #(price) where status <> '0' and book_id = #(book_id)
	#end

	#sql("findByBarcode")
	    select * from book_storage_item_bar_code where del = 0 and school_code = #para(school_code) and bar_code = #para(bar_code)
	#end

	#sql("deleteByBarcode")
	    update book_storage_item_bar_code set del = 1 where school_code = #para(school_code) and bar_code = #para(bar_code)
	#end

	#sql("queryByItemId")
	    select a.* from book_storage_item_bar_code a left join book_storage_item b on a.book_storage_item_id = b.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        and b.id = #para(book_storage_item_id)
	        order by bar_code desc
	#end

    #sql("getItemByName")
	    select a.book_storage_id, a.book_storage_item_id, a.bar_code, b.book_name, b.author, b.publisher, b.price, c.name
	    , c.create_time, c.create_user_code, c.create_user_name
	    from book_storage_item_bar_code a, book_storage_item b, book_storage c
        where a.del = 0 and b.del = 0 and c.del = 0 and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code)
        #if(name)
          and c.name like ('%' + #para(name) + '%')
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
	#end

	#sql("getItemByNameCnt")
        select count(1) total_cnt
        from book_storage_item_bar_code a, book_storage_item b, book_storage c
        where a.del = 0 and b.del = 0 and c.del = 0 and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code)
        #if(name)
          and c.name like ('%' + #para(name) + '%')
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getItemByNameAmount")
        select sum(b.price) total_amount
        from book_storage_item_bar_code a, book_storage_item b, book_storage c
        where a.del = 0 and b.del = 0 and c.del = 0 and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code)
        #if(name)
          and c.name like ('%' + #para(name) + '%')
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

	#sql("getBooksIn")
        select a.book_storage_id, a.book_storage_item_id, a.bar_code, b.book_name, b.author, b.publisher, b.price, c.name
        , c.create_time, c.create_user_code, c.create_user_name, b.catalog_name, count(d.bar_code) borrow_cnt, a.book_id
        from book_storage_item b, book_storage c, book_storage_item_bar_code a left join borrow_book d on d.del = 0  and a.bar_code = d.bar_code
        where a.del = 0 and b.del = 0 and c.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code) and c.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
        group by a.book_storage_id, a.book_storage_item_id, a.bar_code, b.book_name, b.author, b.publisher, b.price, c.name
                , c.create_time, c.create_user_code, c.create_user_name, b.catalog_name, a.book_id
    #end

    #sql("getBooksInCnt")
        select count(1) cnt
        from book_storage_item b, book_storage c, book_storage_item_bar_code a
        where a.del = 0 and b.del = 0 and c.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code) and c.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksInAmount")
        select isnull(sum(b.price),0) amount
        from book_storage_item b, book_storage c, book_storage_item_bar_code a
        where a.del = 0 and b.del = 0 and c.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code) and c.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrow")
        select a.book_storage_id, a.book_storage_item_id, a.bar_code, b.book_name, b.author, d.borrower, isnull(d.dpt_name,'') dpt_name
            , isnull(d.grd_name,'') grd_name, isnull(d.cls_name,'') cls_name, d.borrow_time, d.return_time, d.over_days, a.book_id
        from book_storage_item b, book_storage c, book_storage_item_bar_code a, borrow_book d
        where a.del = 0 and b.del = 0 and c.del = 0 and d.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and c.status = 1 and d.return_status = 0
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrowCnt")
        select count(1) cnt
        from book_storage_item b, book_storage c, book_storage_item_bar_code a, borrow_book d
        where a.del = 0 and b.del = 0 and c.del = 0 and d.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and c.status = 1 and d.return_status = 0
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrowAmount")
        select isnull(sum(b.price),0) amount
        from book_storage_item b, book_storage c, book_storage_item_bar_code a, borrow_book d
        where a.del = 0 and b.del = 0 and c.del = 0 and d.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and c.status = 1 and d.return_status = 0
        #if(catalog_id)
          and b.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,c.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,c.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or b.book_name like ('%' + #para(keyword) + '%')
          or b.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBookInfoByBar")
        select a.bar_code, a.check_no, b.book_name, b.author, b.publisher, b.publish_date, b.price, b.catalog_name, a.create_time, a.create_user_name
        from book_storage_item b, book_storage c, book_storage_item_bar_code a
        where a.del = 0 and b.del = 0 and c.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id
        and a.school_code = #para(school_code) and a.bar_code = #para(bar_code)
    #end

    #sql("getBookBorrowList")
        select a.book_storage_id, a.book_storage_item_id, a.bar_code, b.book_name, b.author, d.borrower, isnull(d.dpt_name,'') dpt_name
        , isnull(d.grd_name,'') grd_name, isnull(d.cls_name,'') cls_name, d.borrow_time, isnull(d.return_time,'') return_time, d.over_days, a.book_id, d.return_status
        from book_storage_item b, book_storage c, book_storage_item_bar_code a, borrow_book d
        where a.del = 0 and b.del = 0 and c.del = 0 and d.del = 0
        and a.book_storage_item_id = b.id and a.book_storage_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and a.bar_code = #para(bar_code)
        and c.status = 1
    #end

#end