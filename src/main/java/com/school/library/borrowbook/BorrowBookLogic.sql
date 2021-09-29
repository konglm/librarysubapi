#namespace("BorrowBookLogic")
#sql("getUserInfoById")
    WITH t AS (
        SELECT COUNT(1) as borrowNum
            #if(stu_code)
             ,stu_code
            #end
            #if(user_code)
             ,user_code
            #end
        FROM borrow_book
        WHERE
        return_status = 0
        #if(stu_code)
          AND   stu_code = #para(stu_code)
        #end
        #if(user_code)
          AND   user_code = #para(user_code)
        #end
          AND del = 0  GROUP BY
        #if(stu_code)
          stu_code
        #end
        #if(user_code)
          user_code
        #end
         )
    SELECT u.*,ISNULL(t.borrowNum,0) as borrowNum FROM user_info u
    LEFT JOIN t  ON
        #if(stu_code)
        t.stu_code = u.stu_code
        #end
        #if(user_code)
        t.user_code = u.user_code
        #end

    WHERE  u.del = 0
        #if(stu_code)
            AND u.stu_code = #para(stu_code)
        #end
        #if(user_code)
            AND u.user_code = #para(user_code)
        #end
    #end

    #sql("queryUnReturnByUser")
        select a.* from borrow_book a where a.del = 0 and a.school_code  = #para(school_code)
        and a.return_status = #para(return_status)
        #if(stu_code)
            and a.stu_code = #para(stu_code)
        #end
        #if(user_code)
            and a.user_code = #para(user_code)
        #end
    #end

    #sql("checkBorrow")
        select a.bar_code from borrow_book a
        where del = 0 and school_code  = #para(school_code)
            and a.bar_code = #para(bar_code) and a.return_status = 0
        UNION
        SELECT b.bar_code FROM book_damaged b where b.del = 0 AND b.unit_code  = #para(school_code)
            and b.bar_code = #para(bar_code)  AND b.last_status <> 1
        UNION
        SELECT c.bar_code FROM book_bar_code c WHERE c.del = 0 and school_code  = #para(school_code)
            AND c.bar_code = #para(bar_code) AND c.status <> 1
    #end

    #sql("queryReturnByUser")
        select a.*, b.judge from borrow_book a left join book_damaged b on b.del = 0 and a.id = b.borrow_id
        where a.del = 0 and a.school_code  = #para(school_code)
        and a.return_status != #para(return_status)
        #if(stu_code)
            and a.stu_code = #para(stu_code)
        #end
        #if(user_code)
            and a.user_code = #para(user_code)
        #end
    #end

    #sql("statisticsTotal")
        select count(1) as borrow_count from borrow_book a
        where a.del = 0 and a.school_code  = #para(school_code)
            #if(begintime)
			  AND CONVERT(varchar,a.borrow_time,23) >= #para(begintime)
			#end
			#if(endtime)
			  AND CONVERT(varchar,a.borrow_time,23) <= #para(endtime)
			#end
    #end

    #sql("statisticsByCatalogNo")
        select * from (
            select a.catalog_no, a.catalog_name, count(1) as borrow_count from borrow_book a
            where a.del = 0 and a.school_code  = #para(school_code)
                #if(begintime)
                  AND CONVERT(varchar,a.borrow_time,23) >= #para(begintime)
                #end
                #if(endtime)
                  AND CONVERT(varchar,a.borrow_time,23) <= #para(endtime)
                #end
            group by a.catalog_no, a.catalog_name

        ) t order by borrow_count desc
    #end

    #sql("queryOver")
        select a.* from borrow_book a
        where a.del = 0 and a.school_code  = #para(school_code)
        and a.return_status = #para(return_status)
        AND CONVERT(varchar,a.borrow_time,23) < #para(limit_time)
        order by id desc
    #end

    #sql("statisticsByBook")
        select * from (
            select a.catalog_no, a.book_name, a.author, a.publisher, a.publish_date, count(1) as borrow_count from borrow_book a
            where a.del = 0 and a.school_code  = #para(school_code)
                #if(begintime)
                  AND CONVERT(varchar,a.borrow_time,23) >= #para(begintime)
                #end
                #if(endtime)
                  AND CONVERT(varchar,a.borrow_time,23) <= #para(endtime)
                #end
            group by a.catalog_no, a.book_name, a.author, a.publisher, a.publish_date
        ) t order by borrow_count desc
    #end

    #sql("statisticsBorrowZeroByBook")
            select catalog_no, book_name, author, publisher, publish_date from book b where
            del = 0 and school_code  = #para(school_code) and
            catalog_no + '_' + book_name + '_' + author + '_' + publisher + '_' + CONVERT(varchar(100), publish_date, 23)
            not in (
                select a.catalog_no + '_' + a.book_name + '_' + a.author + '_' + a.publisher + '_' + CONVERT(varchar(100), a.publish_date, 23) from borrow_book a
                where a.del = 0 and a.school_code  = #para(school_code)
                    #if(begintime)
                      AND CONVERT(varchar,a.borrow_time,23) >= #para(begintime)
                    #end
                    #if(endtime)
                      AND CONVERT(varchar,a.borrow_time,23) <= #para(endtime)
                    #end
            ) order by id desc
        #end

    #sql("PayBookByBarCode")
        SELECT  bb.*
        FROM borrow_book bb
        WHERE  bb.return_status = '0' and bb.bar_code = #para(bar_code)
    #end

    #sql("paybookList")
        SELECT  bb.*
        FROM borrow_book bb
        WHERE  bb.return_status = '0' and bb.school_code = #para(school_code)
        #if(user_type == 'stu')
          and bb.stu_code = #(user_code)
          #else
          and bb.user_code = #(user_code)
        #end
    #end

    #sql("payBook")
        SELECT  bb.*
        FROM borrow_book bb
        WHERE  bb.return_status = '0' and bb.school_code = #para(school_code)
            #if(user_type == 'stu')
                and bb.stu_code = #(user_code)
            #else
                 and bb.user_code = #(user_code)
            #end
            #if(barCodes)
                  and bb.bar_code in (
                  #for(barCode : barCodes )
            #(for.first ? "" : ",")#para(barCode)
            #end )
            #end
    #end

    #sql("checkBookStatus")
    select bar_code,book_name from borrow_book where del = 0
    and school_code  = #para(school_code)

        #if(status == 0)
        and return_status = #para(status)
        #else
        and return_status in ( 0,2,3,4)
        #end
        #if(barCodes)
            and bar_code in (
            #for(barCode : barCodes )
            #(for.first ? "" : ",")#para(barCode)
            #end )
        #end
    #end

    #sql("depositList")
        select bar_code, book_name, over_days, book_status, deductions, borrower, isnull(dpt_name,'') dpt_name, isnull(grd_name,'') grd_name
        , isnull(cls_name,'') cls_name, return_status
        from borrow_book where del = 0 and school_code = #para(school_code)
        #if(start_time)
        and update_time > #para(start_time)
        #end
        #if(end_time)
        and update_time < #para(end_time)
        #end
        #if(keywords)
        and charindex(#para(keywords),ISNULL(sno, '')+ISNULL(borrower, ''))>0
        #end
        order by update_time desc
    #end

     #sql("getTotalDepositAmount")
        select sum(deductions) total_amount
        from borrow_book where del = 0 and school_code = #para(school_code)
        #if(start_time)
        and update_time > #para(start_time)
        #end
        #if(end_time)
        and update_time < #para(end_time)
        #end
        #if(keywords)
        and charindex(#para(keywords),ISNULL(sno, '')+ISNULL(borrower, ''))>0
        #end
    #end

    #sql("statisticsBorrowCnt")
        select count(1) borrow_cnt
        from borrow_book
        where del = 0 and school_code = #para(school_code)
        #if(begin_time)
            and borrow_time > #para(begin_time)
        #end
        #if(end_time)
            and borrow_time < #para(end_time)
        #end
    #end

    #sql("statisticsReturnCnt")
        select count(1) return_cnt
        from borrow_book
        where del = 0 and school_code = #para(school_code)
        #if(begin_time)
            and return_time > #para(begin_time)
        #end
        #if(end_time)
            and return_time < #para(end_time)
        #end
    #end
#end