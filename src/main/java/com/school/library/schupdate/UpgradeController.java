package com.school.library.schupdate;

import com.jfinal.aop.Inject;
import com.jfnice.core.JFniceBaseController;

public class UpgradeController extends JFniceBaseController {
    @Inject
    UpgradeLogic upgradeLogic;

    public void upgrade() {

        String schCodes = getPara("sch_codes");
        upgradeLogic.update(schCodes);
        ok("更新成功");
    }
}
