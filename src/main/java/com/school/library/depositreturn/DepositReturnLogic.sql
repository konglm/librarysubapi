#namespace("DepositReturnLogic")
    #sql("depositList")
        select * from deposit_return where del = 0 and school_code = #para(school_code)
        #if(start_time)
        and create_time > #para(start_time)
        #end
        #if(end_time)
        and create_time < #para(end_time)
        #end
        #if(keywords)
        and charindex(#para(keywords),ISNULL(sno, '')+ISNULL(user_name, ''))>0
        #end
        order by create_time desc
    #end

    #sql("getTotalDepositAmount")
        select sum(return_amount) total_amount from deposit_return where del = 0 and school_code = #para(school_code)
        #if(start_time)
        and create_time > #para(start_time)
        #end
        #if(end_time)
        and create_time < #para(end_time)
        #end
        #if(keywords)
        and charindex(#para(keywords),ISNULL(sno, '')+ISNULL(user_name, ''))>0
        #end
    #end
#end