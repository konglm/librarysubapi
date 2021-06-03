#namespace("BorrowSettingLogic")
    #sql("queryBySchool")
	    select a.* from borrow_setting a
	        where a.del = 0 and a.school_code = #para(school_code) and a.source = #para(source)
	#end

	#sql("queryBySource")
	    select a.* from borrow_setting a
	        where a.del = 0 and a.source = #para(source)
	#end

	#sql("queryById")
	    select a.* from borrow_setting a
	        where a.del = 0 and a.school_code = #para(school_code) and a.id = #para(id)
	#end

#end