#namespace("Dict")
	
	#sql("queryRoleByDictId")
		SELECT b.id, b.name FROM dict_user_type_role AS a LEFT JOIN role AS b ON a.role_id = b.id WHERE a.dict_id	= ?
	#end
	
	#sql("clearRole")
		DELETE FROM dict_user_type_role WHERE dict_id = ?
	#end
	
#end