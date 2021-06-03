package com.school.api.jsy;

import com.alibaba.fastjson.JSON;
import com.jfinal.server.undertow.UndertowServer;
import com.jfnice.core.JFniceMainConfig;
import org.junit.Test;

public class JsyApiTest{

    static {
        UndertowServer.start(JFniceMainConfig.class);
    }

    @Test
    public void getSysPerList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysPerList()));
    }

    @Test
    public void testGetSysPerList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysPerList()));
    }

    @Test
    public void getSysCollList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysCollList()));
    }

    @Test
    public void getSysMajorList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysMajorList()));
    }

    @Test
    public void getSysGradeList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysGradeList()));
    }

    @Test
    public void getSysFascList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysFascList()));
    }

    @Test
    public void getSysMaterList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysMaterList()));
    }

    @Test
    public void getSysSubList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysSubList()));
    }

    @Test
    public void getSysArtList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysArtList()));
    }

    @Test
    public void getSysTermList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysTermList()));
    }

    @Test
    public void getSysMcTypeList() {
        System.out.println(JSON.toJSONString(JsyApi.getSysMcTypeList(-1)));
    }
}