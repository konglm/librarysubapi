package com.school.library.schupdate;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.school.api.model.Grd;

import java.util.List;
import java.util.stream.Collectors;

public class UpgradeService {

    public void update(String schId, List<Grd> grdList) {
        Kv kv = Kv.by("schoolCode", schId).set("grdList", grdList).set("grdCodes", grdList.stream().map(Grd::getGrdCode).collect(Collectors.joining(",")));
        Db.template("UpgradeLogic.upgradeBorrowBook", kv).update();
        Db.template("UpgradeLogic.upgradeDepositRecharge", kv).update();
        Db.template("UpgradeLogic.upgradeDepositReturn", kv).update();
        Db.template("UpgradeLogic.upgradeUserInfo", kv).update();
    }
}
