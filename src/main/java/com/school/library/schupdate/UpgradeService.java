package com.school.library.schupdate;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.school.api.model.Grd;

import java.util.List;
import java.util.stream.Collectors;

public class UpgradeService {

    public void update(String schId, List<Grd> grdList) {
        if(grdList.size() > 0) {
            Kv kv = Kv.by("schoolCode", schId).set("grdList", grdList).set("grdCodes", grdList.stream().map(Grd::getGrdCode).collect(Collectors.joining(",")));
            Db.template("UpgradeLogic.upgradeBorrowBook", kv).update();
            Db.template("UpgradeLogic.upgradeDepositRecharge", kv).update();
            Db.template("UpgradeLogic.upgradeDepositReturn", kv).update();
            Db.template("UpgradeLogic.upgradeUserInfo", kv).update();

            //将毕业班移动到历史表
            List<Grd> grdListFinish = grdList.stream().filter(grd -> grd.getIsFinish() == 1).collect(Collectors.toList());
            if (grdListFinish.size() > 0) {
                Kv kvHis = Kv.by("schoolCode", schId).set("grdCodes", grdList.stream().filter(grd ->
                        grd.getIsFinish() == 1).map(Grd::getGrdCode).collect(Collectors.joining(",")));
                Db.template("UpgradeLogic.hisBorrowBook", kvHis).update();
                Db.template("UpgradeLogic.hisDepositRecharge", kvHis).update();
                Db.template("UpgradeLogic.hisDepositReturnn", kvHis).update();
                Db.template("UpgradeLogic.hisUserInfo", kvHis).update();
                //删除毕业班数据
                Db.template("UpgradeLogic.delHisBorrowBook", kvHis).update();
                Db.template("UpgradeLogic.delHisDepositRecharge", kvHis).update();
                Db.template("UpgradeLogic.delHisDepositReturn", kvHis).update();
                Db.template("UpgradeLogic.delHisUserInfo", kvHis).update();
            }
        }
    }
}
