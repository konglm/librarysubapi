package com.school.demo;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CurrentUser;
import com.school.api.map.*;

public class IndexController extends JFniceBaseController {

    @Clear
    public void index() {
        System.out.println(JsonKit.toJson(SysAreaMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysArtMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysCollMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysFascMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysGradeMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysMajorMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysMaterMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysMcTypeMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysPerMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysSubMap.me.toArray()));
        System.out.println(JsonKit.toJson(SysTermMap.me.toArray()));
        System.out.println(JsonKit.toJson(YearMap.me.toArray()));
        ok("index");
    }

    public void cache() {
        System.out.println(CurrentUser.getPlatformCode());
        System.out.println(CurrentUser.getAppCode());
        System.out.println(CurrentUser.getSchoolCode());
        System.out.println(JsonKit.toJson(ArtMap.me.toArray()));
        System.out.println(JsonKit.toJson(ClsMap.me.toArray()));
        System.out.println(JsonKit.toJson(DptMap.me.toArray()));
        System.out.println(JsonKit.toJson(GrdMap.me.toArray()));
        System.out.println(JsonKit.toJson(PerMap.me.toArray()));
        System.out.println(JsonKit.toJson(StuMap.me.getSchStuList()));
        System.out.println(JsonKit.toJson(SubArtMap.me.toArray()));
        System.out.println(JsonKit.toJson(SubMap.me.toArray()));
        ok("index");
    }

    @JsyPermissions(OpCodeEnum.ADD)
    public void add() {
        ok("add");
    }

    @JsyPermissions(OpCodeEnum.EDIT)
    public void edit() {
        ok("edit");
    }

    @JsyPermissions(OpCodeEnum.DELETE)
    public void delete() {
        ok("delete");
    }

    @JsyPermissions
    public void page() {
        ok("page");
    }

}
