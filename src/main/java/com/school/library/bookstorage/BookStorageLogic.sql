#namespace("BookStorageLogic")
    #sql("queryIndexList")
	    select a.*, c.book_count from book_storage a left join
            (select book_storage_id, ISNULL(COUNT(1), 0) as book_count from book_storage_item_bar_code b where b.del = 0
                and b.school_code = #para(school_code)
	            group by b.book_storage_id
            ) c on c.book_storage_id = a.id
	        where a.del = 0 and a.school_code = #para(school_code)
	        #if(status)
	            and a.status = #para(status)
	        #end
	        #if(name)
	            and a.name LIKE ('%' + #para(name) + '%')
	        #end
	        #if(begintime)
	           AND CONVERT(varchar,a.create_time,23) >= #para(begintime)
	        #end
	        #if(endtime)
	            AND CONVERT(varchar,a.create_time,23) <= #para(endtime)
	        #end
	        order by a.id desc
	#end

	#sql("queryById")
	    select a.* from book_storage a
	        where a.del = 0 and a.school_code = #para(school_code) and a.id = #para(id)
	#end

	#sql("queryByStatus")
	    select a.* from book_storage a
	        where a.del = 0 and a.school_code = #para(school_code) and a.status = #para(status)
	#end

    #sql("statisticsTotalStorage")
        select count(1) total_storage_cnt, sum(price) total_storage_amount
        from book_storage_item_bar_code
        where del = 0 and school_code = #para(school_code)
        and status in (1,2,3,4,6)
    #end

    #sql("statisticsTotalDamage")
        select count(1) total_storage_cnt, sum(price) total_storage_amount
        from book_storage_item_bar_code
        where del = 0 and school_code = #para(school_code)
        and status = 3
    #end

    #sql("statisticsTotalLose")
        select count(1) total_storage_cnt, sum(price) total_storage_amount
        from book_storage_item_bar_code
        where del = 0 and school_code = #para(school_code)
        and status = 4
    #end

    #sql("statisticsTotalWriteOff")
        select count(1) total_storage_cnt, sum(price) total_storage_amount
        from book_storage_item_bar_code
        where del = 0 and school_code = #para(school_code)
        and status = 6
    #end


#end