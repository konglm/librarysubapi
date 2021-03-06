package com.school.library.schupdate;

import com.jfinal.aop.Inject;
import com.school.api.gx.RsApi;
import com.school.api.model.Grd;

import java.util.Arrays;
import java.util.List;

public class UpgradeLogic {
    @Inject
    private UpgradeService upgradeService;

    public void update(String schCodes) {
        List<String> schList = Arrays.asList(schCodes.split(","));
        schList.stream().forEach(schId -> {
            List<Grd> grdList = RsApi.getGrdList(schId, -1);

            upgradeService.update(schId, grdList);
        });
    }

}
