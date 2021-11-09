#namespace("BookLogic")
    #sql("queryByBookInfo")
	    select a.* from book a
	        where a.del = 0 and a.school_code = #para(school_code)
	        #if(id)
	            and a.id = #para(id)
	        #end
	        #if(book_storage_item_id)
	            and a.book_storage_item_id = #para(book_storage_item_id)
	        #end
	        #if(catalog_name)
	            and a.catalog_name = #para(catalog_name)
	        #end
	        #if(book_name)
	            and a.book_name = #para(book_name)
	        #end
	        #if(author)
	            and a.author = #para(author)
	        #end
	        #if(publisher)
	            and a.publisher = #para(publisher)
	        #end
	        #if(publish_date)
	            and a.publish_date = #para(publish_date)
	        #end
	#end

	#sql("logicDeleteByItemId")
	    update book set del = 1, update_user_code = #para(update_user_code), update_time = #para(update_time)
	        where del = 0 and school_code = #para(school_code)
	        and book_storage_item_id = #para(book_storage_item_id)
	#end

	#sql("queryByBookName")
	    select a.*, (select MIN(price) from book_bar_code b where b.del = 0 and b.book_id = a.id) as price from book a
	        where a.del = 0 and a.school_code = #para(school_code)
	        #if(book_name)
	            and a.book_name like ('%' + #para(book_name) + '%')
	        #end
	        order by a.id desc
	#end

	#sql("queryByBarCode")
	    select b.*, a.bar_code, a.price from book_bar_code a left join book b on a.book_id = b.id
	        where a.del = 0 and b.del = 0 and a.school_code = #para(school_code)
	        and a.bar_code = #para(bar_code)
	#end

	#sql("booksearch")
	    SELECT * FROM (
            SELECT
                a.*, ISNULL(b.borrowing,0) AS borrowing, ISNULL(b.borrowd_num,0) AS borrowd_num, ISNULL(c.total_num,0) AS total_num
                #if(keywords)
                    , CASE WHEN a.book_name = #para(keywords) THEN 1
                           WHEN a.book_name LIKE ('%'+ #para(keywords) +'%') THEN 2
                           ELSE 9
                    END AS p_like
                #end
            FROM book a
                LEFT JOIN (
                    SELECT
                        book_id,
                        SUM(CASE WHEN return_status = 0 THEN 1 ELSE 0 END) AS borrowing,
                        COUNT (1) AS borrowd_num
                    FROM
                        borrow_book
                    WHERE del = 0
                    GROUP BY book_id) b ON a.id = b.book_id
                LEFT JOIN (
                    SELECT book_id,COUNT(bar_code) AS total_num
                    FROM book_bar_code
                    WHERE del = 0 and status = '1'
                    GROUP BY book_id) AS c ON a.id = c.book_id
            WHERE a.del = 0
                and c.total_num > 0
                #if(unit_code)
                    AND a.school_code = #para(unit_code)
                #end
                #if(keywords)
                    AND a.book_name LIKE (('%'+ #para(keywords) +'%'))
                #end
                #if(catalogId)
                    AND a.catalog_id IN ( #(catalogId) )
                #end
        ) t
        ORDER BY
            #if(sort)
                #(sort) DESC,
            #end
            #if(keywords)
                p_like ASC,
            #end
            create_time DESC
	#end

	#sql("findByBook")
        WITH T1 AS (SELECT bbc.book_id,
                           sum(CASE WHEN bbc.status = 1  THEN 1 ELSE 0 END)as book_num,
                           avg(bbc.price) as price
                    FROM book_bar_code bbc
                             INNER JOIN book b ON b.id = bbc.book_id
                    where bbc.del = 0
                    GROUP BY bbc.book_id)
        select b.*,T1.price,T1.book_num  from  book b
        left join T1 on T1.book_id = b.id
        where b.del = 0 and b.id =#(id)
	#end

	#sql("barInfoByBook")
        SELECT bbc.bar_code, bb.id as borrow_id, bb.borrow_time,bb.school_code,bb.grd_code,bb.grd_name,bb.cls_code,bb.cls_name,
               bb.stu_code,bb.sno,bb.borrower,bb.dpt_code,bb.dpt_name,bb.catalog_name,bb.check_no,bb.book_name,bb.price,bb.return_status,bb.user_code,
               bb.book_id
        FROM book_bar_code bbc
         LEFT JOIN borrow_book bb ON bbc.bar_code = bb.bar_code AND bb.return_status = '0'
        WHERE bbc.book_id = #(id) and bbc.del = 0 and bbc.status = '1'
	#end

	#sql("queryByCatalogIds")
	    select a.* from book a
	        where a.del = 0 and a.school_code = #para(school_code)
	        and a.catalog_id in (#(catalog_ids))
	#end

	#sql("getBooksIn")
        select a.book_id, a.bar_code, a.check_no, c.book_name, c.author, c.publisher, a.price
        , a.create_time, a.create_user_code, a.create_user_name, c.catalog_name, count(a.bar_code) borrow_cnt
        from book c, book_bar_code a left join borrow_book d on d.del = 0  and a.bar_code = d.bar_code
        where a.del = 0 and c.del = 0 and a.book_id = c.id
        and a.school_code = #para(school_code) and a.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
        group by a.book_id, a.bar_code, a.check_no, c.book_name, c.author, c.publisher, a.price
                , a.create_time, a.create_user_code, a.create_user_name, c.catalog_name
    #end

    #sql("getBooksInCnt")
        select count(1) cnt
        from book c, book_bar_code a
        where a.del = 0 and c.del = 0 and a.book_id = c.id
        and a.school_code = #para(school_code) and a.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksInAmount")
        select isnull(sum(a.price),0) amount
        from book c, book_bar_code a
        where a.del = 0 and c.del = 0 and a.book_id = c.id
        and a.school_code = #para(school_code) and a.status = 1 and a.bar_code not in (select bar_code from borrow_book where return_status = 0 and del = 0)
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrow")
        select a.book_storage_id, a.book_storage_item_id, a.bar_code, a.check_no, c.book_name, c.author, d.borrower, isnull(d.dpt_name,'') dpt_name
            , isnull(d.grd_name,'') grd_name, isnull(d.cls_name,'') cls_name, d.borrow_time, d.return_time, d.over_days, a.book_id
        from book c, book_bar_code a, borrow_book d
        where a.del = 0 and c.del = 0 and d.del = 0
        and a.book_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and a.status = 1 and d.return_status = 0
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrowCnt")
        select count(1) cnt
        from book c, book_bar_code a, borrow_book d
        where a.del = 0 and c.del = 0 and d.del = 0
        and a.book_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and a.status = 1 and d.return_status = 0
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBooksBorrowAmount")
        select isnull(sum(a.price),0) amount
        from book c, book_bar_code a, borrow_book d
        where a.del = 0 and c.del = 0 and d.del = 0
        and a.book_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and a.status = 1 and d.return_status = 0
        #if(catalog_id != -1)
          and c.catalog_id = #para(catalog_id)
        #end
        #if(begin_time)
          AND CONVERT(varchar,a.create_time,23) >= #para(begin_time)
        #end
        #if(end_time)
          AND CONVERT(varchar,a.create_time,23) <= #para(end_time)
        #end
        #if(is_over_day == 1)
          AND d.over_days > 0
        #end
        #if(keyword)
          and (a.bar_code like ('%' + #para(keyword) + '%') or c.book_name like ('%' + #para(keyword) + '%')
          or c.author like ('%' + #para(keyword) + '%'))
        #end
    #end

    #sql("getBookInfoByBar")
        select a.bar_code, a.check_no, c.book_name, c.author, c.publisher, c.publish_date, a.price, c.catalog_name, c.book_img_url
        , a.create_time, a.create_user_name
        from book c, book_bar_code a
        where a.del = 0 and c.del = 0 and a.book_id = c.id
        and a.school_code = #para(school_code) and a.bar_code = #para(bar_code)
    #end

    #sql("getBookBorrowList")
        select a.book_id, a.bar_code, c.book_name, c.author, d.borrower, isnull(d.dpt_name,'') dpt_name
        , isnull(d.grd_name,'') grd_name, isnull(d.cls_name,'') cls_name, d.borrow_time, isnull(d.return_time,'') return_time
        , d.over_days, d.return_status
        from book c, book_bar_code a, borrow_book d
        where a.del = 0 and c.del = 0 and d.del = 0
        and a.book_id = c.id and a.bar_code = d.bar_code
        and a.school_code = #para(school_code) and a.bar_code = #para(bar_code)
        and a.status = 1
    #end

#end