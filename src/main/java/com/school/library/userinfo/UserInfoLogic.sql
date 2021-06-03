#namespace("UserInfoLogic")

    #sql("queryStuIndexList")
	    select a.*, ISNULL(c.borrow_count, 0) as borrow_count from user_info a left join
            (select stu_code, ISNULL(COUNT(1), 0) as borrow_count from borrow_book b where b.del = 0
                and b.school_code = #para(school_code) and b.return_status = #para(return_status)
	            group by b.stu_code
            ) c on c.stu_code = a.stu_code
	        where a.del = 0 and a.school_code = #para(school_code) and a.user_type = #para(user_type)
	        #if(grd_code)
	            and a.grd_code = #para(grd_code)
	        #end
	        #if(cls_code)
	            and a.cls_code = #para(cls_code)
	        #end
	        #if(keyword)
	            and ( a.stu_name LIKE ('%' + #para(keyword) + '%') or a.card_no LIKE ('%' + #para(keyword) + '%')
	                or a.sno LIKE ('%' + #para(keyword) + '%')
	            )
	        #end
	        order by a.grd_code, a.cls_code, a.stu_name
	#end

	#sql("queryTeacherIndexList")
	    select a.*, ISNULL(c.borrow_count, 0) as borrow_count from user_info a left join
            (select user_code, ISNULL(COUNT(1), 0) as borrow_count from borrow_book b where b.del = 0
                and b.school_code = #para(school_code) and b.return_status = #para(return_status)
	            group by b.user_code
            ) c on c.user_code = a.user_code
	        where a.del = 0 and a.school_code = #para(school_code) and a.user_type = #para(user_type)
	        #if(dpt_code)
	            and a.dpt_code = #para(dpt_code)
	        #end
	        #if(keyword)
	            and ( a.user_name LIKE ('%' + #para(keyword) + '%') or a.card_no LIKE ('%' + #para(keyword) + '%') )
	        #end
	        order by a.dpt_code, a.user_name
	#end

    #sql("queryByClsCodes")
	    select a.* from user_info a
	        where a.del = 0 and a.school_code = #para(school_code) and a.cls_code in (#(cls_codes))
	#end

	#sql("deleteNotClsCodes")
	    update user_info set del = 1 , update_user_code = #para(update_user_code), update_time = #para(update_time)
	        where del = 0 and school_code = #para(school_code) and cls_code not in (#(cls_codes))
	#end

	#sql("queryByType")
	    select a.* from user_info a
	        where a.del = 0 and a.school_code = #para(school_code) and a.user_type = #para(user_type)
	#end

	#sql("queryUser")
	    select a.* from user_info a
	        where a.del = 0 and a.school_code = #para(school_code)
	        #if(grd_code)
	            and a.grd_code = #para(grd_code)
	        #end
	        #if(cls_code)
	            and a.cls_code = #para(cls_code)
	        #end
	        #if(stu_code)
	            and a.stu_code = #para(stu_code)
	        #end
	        #if(dpt_code)
	            and a.dpt_code = #para(dpt_code)
	        #end
	        #if(user_code)
	            and a.user_code = #para(user_code)
	        #end
	#end

	#sql("queryById")
	    select a.* from user_info a
	        where a.del = 0 and a.school_code = #para(school_code)
	        and id = #para(id)
	#end

	#sql("updateDeposit")
	    update user_info set deposit = #para(deposit), update_user_code = #para(update_user_code), update_time = #para(update_time),
	        version = version + 1
	        where school_code =  #para(school_code) and id = #para(id) and version = #para(version)
	#end

    #sql("findByUserCode")
        SELECT  *
        FROM user_info
        WHERE  del = 0 and school_code = #para(school_code)
            #if(user_type == 'stu')
            and stu_code = #(user_code)
            #else
            and user_code = #(user_code)
            #end
    #end

#end