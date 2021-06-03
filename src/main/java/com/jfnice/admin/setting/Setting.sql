#namespace("Setting")
	
	#sql("deleteAllByAccess")
		DELETE FROM setting WHERE access = ?
	#end
	
	#sql("getCustomListByAccess")
		SELECT *
		FROM setting
		WHERE user_id > 0
		  AND access = ?
	#end
	
#end