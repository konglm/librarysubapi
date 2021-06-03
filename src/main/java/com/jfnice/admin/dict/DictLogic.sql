#namespace("DictLogic")
	
	#sql("queryPageOrList")
		SELECT *
		FROM dict
		WHERE del = 0
		#if( title )
		  AND title = #para(title)
		#end
		#if( tag )
		  AND tag = #para(tag)
		#end
		#if( keyword )
		  AND ( k LIKE ('%' + #para(keyword) + '%') OR v LIKE ('%' + #para(keyword) + '%') )
		#end
		#if( orders )
		ORDER BY #(orders)
		#else
		ORDER BY sort ASC, id ASC
		#end
	#end
	
	#sql("queryDistinctTagList")
		SELECT tag, MIN(sort) AS min_sort
		FROM dict 
		WHERE del = 0
		GROUP BY tag
		ORDER BY min_sort ASC
	#end
	
#end