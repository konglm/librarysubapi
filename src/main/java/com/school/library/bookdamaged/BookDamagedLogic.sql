#namespace("BookDamagedLogic")

    #sql("queryBysch")
    select * from book_damaged where del = 0
    and unit_code = #para(school_code)
        #if(keywords)
        and charindex(#para(keywords),ISNULL(book_name, '')+ISNULL(author, '')+ISNULL(bar_code, ''))>0
        #end
        #if(book_status)
        and book_status = #para(book_status)
        #end
        #if(repair_type)
        and book_status = 2 and last_status = #para(repair_type)
        #end
        order by record_time DESC
    #end

    #sql("queryDetail")
        select bd.book_status,bd.deductions,bd.explain,bd.recorder,
               bd.record_time,bd.last_status,bd.repairer,bd.repair_time,
               bb.bar_code,bb.book_name,bb.author,bb.publisher,
               bb.publish_date,bb.price,bb.catalog_name,bb.check_no,
               bb.borrower,bb.cls_name,bb.grd_name,bb.sno,bb.dpt_name,
               bb.book_img_url
        from book_damaged bd
                 LEFT JOIN borrow_book bb ON bb.id = bd.borrow_id
        WHERE  bd.id = #(id)
    #end

    #sql("queryCheckList")
        select  a.*,bd.book_status,bd.recorder,bd.record_time,bd.last_status,bd.deductions,bd.id,bd.judge,bd.explain,bd.judge_time
        from book_damaged bd
        LEFT JOIN (
            SELECT DISTINCT bb.id AS borrow_id,bb.bar_code,bb.book_name,bb.author,bb.publisher,
                            bb.publish_date,bb.price,bb.catalog_name,bb.check_no,
                            bb.borrower,bb.cls_name,bb.grd_name,bb.sno,bb.dpt_name,
                            ui.mobile ,ui.sex,ui.user_code,ui.stu_code,ui.user_type   FROM borrow_book bb
            LEFT JOIN  user_info ui  ON  bb.stu_code = ui.stu_code  OR bb.user_code = ui.user_code
        ) AS a ON a.borrow_id = bd.borrow_id
        where bd.del = 0
        #if(judge)
        #if(judge == '0')
        and bd.judge = 0
        #else
        and bd.judge in ( 1,2 )
        #end
        #end
        #if(book_status)
        and bd.book_status = #para(book_status)
        #end
        #if(unit_code)
        and bd.unit_code = #para(unit_code)
        #end
        #if(start_time)
        and  bd.record_time >= #para(start_time)
        #end
        #if(end_time)
        and  bd.record_time <= #para(end_time)
        #end
        ORDER BY bd.judge_time DESC,bd.record_time DESC
    #end

#end