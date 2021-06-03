package com.jfnice.admin.asset;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.cron4j.ITask;

public class AssetTask implements ITask {

    private static final AssetService assetService = Aop.get(AssetService.class);

    @Override
    public void run() {
        assetService.clear();
    }

    @Override
    public void stop() {

    }
}
