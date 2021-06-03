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

#end