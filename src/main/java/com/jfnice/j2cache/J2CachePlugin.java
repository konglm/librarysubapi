package com.jfnice.j2cache;

import com.jfinal.plugin.IPlugin;

public class J2CachePlugin implements IPlugin {

    @Override
    public boolean start() {
        J2CacheKit.init();
        J2CacheShareKit.init();
//        J2CacheShiroSessionKit.init();
        return true;
    }

    @Override
    public boolean stop() {
        J2CacheKit.destroy();
        J2CacheShareKit.destroy();
//        J2CacheShiroSessionKit.destroy();
        return true;
    }

}
