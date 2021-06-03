#namespace("CatalogLogic")
    #sql("queryById")
	    select a.* from catalog a
	        where a.del = 0 and a.school_code = #para(school_code) and a.id = #para(id)
	#end

	#sql("queryMaxSortByPid")
	    select isnull(max(a.sort), 0) as sort from catalog a
	        where a.del = 0 and a.school_code = #para(school_code) and a.pid = #para(pid)
	#end

	#sql("queryBySchool")
	    select a.* from catalog a
	        where a.del = 0 and a.school_code = #para(school_code) and a.source = #para(source)
	        order by a.pid, a.sort
	#end

	#sql("queryDelBySchool")
	    select a.* from catalog a
	        where a.del = 1 and a.school_code = #para(school_code) and a.source = #para(source)
	#end

	#sql("queryBySource")
	    select a.* from catalog a
	        where a.del = 0 and a.source = #para(source)
	        order by a.pid, a.sort
	#end

	#sql("queryByPid")
	    select * from catalog a
	        where a.del = 0 and a.school_code = #para(school_code) and a.pid = #para(pid)
	#end

	#sql("logicDeleteByIds")
	    update catalog set del = 1, update_time = #para(update_time), update_user_code = #para(update_user_code)
	        where del = 0 and school_code = #para(school_code) and id in (#(ids))
	#end

	#sql("findLeafCatalog")
        WITH tmp AS (SELECT id,pid,catalog_name FROM catalog
             WHERE del = 0
               AND  school_code = #para(unitCode)
               AND pid = #(id)
               or id = #(id)
             UNION ALL
             SELECT BC.id,BC.pid,BC.catalog_name FROM catalog BC, tmp T
             WHERE BC.del = 0
               AND BC.pid = T.id
        )
        SELECT DISTINCT * FROM tmp T
	#end

#end