#namespace("JFniceBaseOld")
	
	#sql("queryAllRelativeList")
		WITH tmp AS ( 
			SELECT *
			FROM #(tableName)
			WHERE is_del = 0
			#if( !showAllStatusFlag )
			  AND status = 1
			#end
			#if( id == 0L )
			  AND #(parentKey) = #para(id)
			#else
			  AND #(primaryKey) = #para(id)
			#end
			UNION ALL
			SELECT a.*
			FROM #(tableName) a
				,tmp b
			WHERE #if( downFlag ) a.#(parentKey) = b.#(primaryKey) #else a.#(primaryKey) = b.#(parentKey) #end
			  AND a.is_del = 0
			#if( !showAllStatusFlag )
			  AND a.status = 1
			#end
		) SELECT #(fields) FROM tmp
	#end

	#sql("queryPageOrList")
		SELECT #(fields)
		FROM #(tableName) 
		#for( x : conditions )
			#( for.first ? "WHERE" : "AND" )
			#(x.key) #para(x.value)
		#end
		#if( orders )
		ORDER BY #(orders)
		#else
		ORDER BY sequence ASC, #(primaryKey) ASC
		#end
	#end
	
	#sql("queryListByIds")
		SELECT #(fields)
		FROM #(tableName) 
		WHERE #(primaryKey) IN ( #(idsStr) )
		#if( orders )
		ORDER BY #(orders)
		#else
		ORDER BY sequence ASC, #(primaryKey) ASC
		#end
	#end
	
	#sql("queryById")
		SELECT #(fields)
		FROM #(tableName)
		WHERE #(primaryKey) = #para(id)
	#end
	
	#sql("deleteById")
		#if( isRealDelete )
			DELETE FROM #(tableName) WHERE #(primaryKey) = ?
		#else
			UPDATE #(tableName) SET is_del = 1 WHERE #(primaryKey) = ?
		#end
	#end
	
	#sql("toggleField")
		UPDATE #(tableName)
		SET #(field) = CASE #(field) WHEN 1 THEN 0 ELSE 1 END
		WHERE #(primaryKey) = ?
	#end
	
	#sql("isUnique")
		SELECT TOP 1 *
		FROM #(tableName)
		WHERE is_del = 0
		#for( field : fields )
		 #if( m.get(field) != null )
		  AND #(field) = #para(m.get(field))
		 #end
		#end
		#if( m.get(primaryKey) != null )
		  AND #(primaryKey) <> #para(m.get(primaryKey))
		#end
	#end
	
	#sql("hasChild")
		SELECT TOP 1 * 
		FROM #(tableName) 
		WHERE is_del = 0
		  AND #(parentKey) = #para(pid)
	#end
	
#end