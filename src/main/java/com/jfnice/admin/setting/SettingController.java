package com.jfnice.admin.setting;

import com.Start;
import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.model.Setting;

@Before(SettingValidator.class)
public class SettingController extends JFniceBaseController {

    @Inject
    private SettingService settingService;

    public void info() {
        Setting setting = settingService.getCurrentSetting(getPara("access"));
        renderJson(JSON.toJSONString(setting));
    }

    public void restore() {
        Setting setting = settingService.getDefaultSetting(getPara("access"));
        renderJson(JSON.toJSONString(setting));
    }

    @Before(Tx.class)
    public void update() {
        Setting setting = getBean(Setting.class, "", true);
        setting.setUserId(Start.JFniceDevMode ? 0L : Long.parseLong(CurrentUser.getUserCode()));
        setting = settingService.saveOrUpdate(setting);
        ok("保存成功！", ResultEnum.SUCCESS.getCode(), JSON.toJSONString(setting));
    }

}