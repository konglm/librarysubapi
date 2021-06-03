#namespace("BookInventoryLogic")

    #sql("queryIndexList")
	    select a.* from book_inventory a
	        where a.del = 0 and a.school_code = #para(school_code)
	        #if(status)
	            and a.status = #para(status)
	        #end
	        #if(name)
	            and a.name like ('%' + #para(name) + '%')
	        #end
	        #if(begintime)
			  AND CONVERT(varchar,a.create_time,23) >= #para(begintime)
			#end
			#if(endtime)
			  AND CONVERT(varchar,a.create_time,23) <= #para(endtime)
			#end
			order by a.id desc
	#end

    #sql("queryByStatus")
	    select a.* from book_inventory a
	        where a.del = 0 and a.school_code = #para(school_code) and a.status = #para(status)
	#end

	#sql("queryById")
	    select a.* from book_inventory a
	        where a.del = 0 and a.school_code = #para(school_code) and a.id = #para(id)
	#end

#end