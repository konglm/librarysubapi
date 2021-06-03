#namespace("SearchLogic")
    #sql("statisticsByKeyWord")
	    select * from (
	        select a.key_word, count(1) as search_count from search a
	            where a.del = 0 and a.school_code = #para(school_code)
	            #if(begintime)
                  AND CONVERT(varchar,a.create_time,23) >= #para(begintime)
                #end
                #if(endtime)
                  AND CONVERT(varchar,a.create_time,23) <= #para(endtime)
                #end
	            group by a.key_word
	    ) t order by search_count
	#end
#end