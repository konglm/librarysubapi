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
    #sql("hisBorrowBook")
        insert into borrow_book_his
        select del, create_time, create_user_code, create_user_name, update_time, update_user_code, school_code, grd_code
        , grd_name, cls_code, cls_name, stu_code, sno, dpt_name, borrower, catalog_name, check_no, price, book_name
        , author, publisher, publish_date, bar_code, borrow_time, return_time, book_status, return_status, over_days
        , deductions, explain, book_img_url, user_code, dpt_code, book_id, catalog_no
        from borrow_book where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("hisDepositRecharge")
        insert into deposit_recharge_his
        select del, create_time, create_user_code, create_user_name, update_time, update_user_code, school_code, grd_code
        , grd_name, cls_code, cls_name, stu_code, sno, dpt_name, user_name, card_no, recharge_amount, recharge_time, user_code
        from deposit_recharge where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("hisDepositReturnn")
        insert into deposit_return_his
        select del, create_time, create_user_code, create_user_name, update_time, update_user_code, school_code, grd_code
        , grd_name, cls_code, cls_name, stu_code, sno, dpt_name, user_name, card_no, return_amount, return_time, user_code
        , dpt_code
        from deposit_return where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("hisUserInfo")
        insert into user_info_his
        select del, create_time, create_user_code, create_user_name, update_time, update_user_code, school_code, grd_code
        , grd_name, cls_code, cls_name, stu_code, sno, stu_name, dpt_code, dpt_name, user_code, user_name, sex, card_no
        , mobile, deposit, img_url, version, user_type
        from user_info where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("delHisBorrowBook")
        update borrow_book
        set del = 1 where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("delHisDepositRecharge")
        update deposit_recharge
        set del = 1 where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("delHisDepositReturn")
        update deposit_return
        set del = 1 where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
    #sql("delHisUserInfo")
        update user_info
        set del = 1 where del = 0 and school_code = #para(schoolCode) and grd_code in (#(grdCodes))
    #end
#end