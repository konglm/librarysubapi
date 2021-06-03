#namespace("Asset")
	
	#sql("queryByUrl")
		SELECT TOP 1 *
		FROM asset
		WHERE url = ?
	#end

	#sql("deleteByUrl")
		DELETE FROM asset WHERE url = ?
	#end
	
	#sql("queryInvalidAssetList")
		SELECT *
		FROM asset
		WHERE saved = 0
	#end

	#sql("clear")
		DELETE FROM asset WHERE saved = 0
	#end
	
#end