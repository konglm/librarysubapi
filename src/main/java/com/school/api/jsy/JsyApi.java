package com.school.api.jsy;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfnice.ext.CurrentUser;
import com.school.api.gx.GxClient;
import com.school.api.gx.GxRequest;
import com.school.api.gx.GxResponse;
import com.school.api.model.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统统一信息接口
 *
 * @author jsy
 */
public class JsyApi {

    private static final GxClient CLIENT = new GxClient(PropKit.get("jsyApiUrl", "").trim());

    /**
     * 1.1:系统科目信息
     */
    public static List<SysSub> getSysSubList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysSubP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysSub.class) : new ArrayList<>();
    }

    /**
     * 1.3:系统分科信息
     */
    public static List<SysArt> getSysArtList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysArtsP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysArt.class) : new ArrayList<>();
    }

    /**
     * 1.5:系统院系信息
     */
    public static List<SysColl> getSysCollList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysCollP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysColl.class) : new ArrayList<>();
    }

    /**
     * 1.7:系统分册信息
     */
    public static List<SysFasc> getSysFascList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysFascP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysFasc.class) : new ArrayList<>();
    }

    /**
     * 1.9:系统专业信息
     */
    public static List<SysMajor> getSysMajorList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysMajorP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysMajor.class) : new ArrayList<>();
    }

    /**
     * 1.11:系统教版信息
     */
    public static List<SysMater> getSysMaterList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysMaterP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysMater.class) : new ArrayList<>();
    }

    /**
     * 1.13:系统设备信息
     *
     * @param isImg 视频设备1,普通设备0,全部-1
     * @return List<SysMcType>
     */
    public static List<SysMcType> getSysMcTypeList(int isImg) {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("is_img", isImg);
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysMcTypeP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysMcType.class) : new ArrayList<>();
    }

    /**
     * 1.15:系统学期信息
     */
    public static List<SysTerm> getSysTermList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysTermP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysTerm.class) : new ArrayList<>();
    }

    /**
     * 1.19:系统学段及年级信息
     */
    public static List<SysPer> getSysPerList() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("page_size", 1);
            put("page_number", -1);
        }};
        GxRequest request = new GxRequest("SysPerP", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysPer.class) : new ArrayList<>();
    }

    /**
     * 1.19:系统年级
     */
    public static List<SysGrade> getSysGradeList() {
        List<SysGrade> sysGradeList = new ArrayList<>();
        List<SysPer> perList = getSysPerList();
        perList.forEach(e -> {
            if (CollectionUtils.isNotEmpty(e.getGrdList())) {
                sysGradeList.addAll(e.getGrdList());
            }
        });
        return sysGradeList;
    }

    /**
     * 1.21:系统地区列表
     *
     * @param type   0所有省份,1城市,2区县,3所有城市,4获取某个城市信息,5所有区域信息
     * @param areano 获取type:1,2,4时必填(填写相关区域代码),其他留空
     * @return List<SysArea>
     */
    public static List<SysArea> getSysAreaList(int type, String areano) {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("type", type);
            put("areano", areano);
        }};
        GxRequest request = new GxRequest("SysArea", paraMap);
        GxResponse response = CLIENT.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SysArea.class) : new ArrayList<>();
    }

}
