#namespace("UpgradeLogic")

    #sql("upgradeBorrowBook")
        update borrow_book
        set grd_name = case grd_code
            #for(g:grdList)
            when '#(g.grdCode)'  then '#(g.grdName)'
            #end
            else grd_name
        end
        where school_code = #para(schoolCode)
        and grd_code in (#(grdCodes))
    #end
    #sql("upgradeDepositRecharge")
        update deposit_recharge
        set grd_name = case grd_code
            #for(g:grdList)
            when '#(g.grdCode)'  then '#(g.grdName)'
            #end
            else grd_name
        end
        where school_code = #para(schoolCode)
        and grd_code in (#(grdCodes))
    #end
    #sql("upgradeDepositReturn")
        update deposit_return
        set grd_name = case grd_code
            #for(g:grdList)
            when '#(g.grdCode)'  then '#(g.grdName)'
            #end
            else grd_name
        end
        where school_code = #para(schoolCode)
        and grd_code in (#(grdCodes))
    #end
    #sql("upgradeUserInfo")
        update user_info
        set grd_name = case grd_code
            #for(g:grdList)
            when '#(g.grdCode)'  then '#(g.grdName)'
            #end
            else grd_name
        end
        where school_code = #para(schoolCode)
        and grd_code in (#(grdCodes))
    #end

#end