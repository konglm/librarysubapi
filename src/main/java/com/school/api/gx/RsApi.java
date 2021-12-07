package com.school.api.gx;

import com.alibaba.fastjson.JSONObject;
import com.jfnice.commons.Config;
import com.jfnice.ext.CurrentUser;
import com.school.api.model.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class RsApi {

    private static final GxClient client = new GxClient(Config.RS_SUB_API_URL);

    /**
     * 1.0 学校学段
     *
     * @return List<Per>
     */
    public static List<Per> getPerList() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("per", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Per.class) : new ArrayList<>();
    }

    /**
     * 1.1 学校年级
     *
     * @param isFinish 是否毕业
     * @return List<Grd>
     */
    public static List<Grd> getGrdList(String schCode, int isFinish) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("sch_code", schCode);
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("is_finish", isFinish);
        }};
        GxRequest request = new GxRequest("grd", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Grd.class) : new ArrayList<>();
    }
    public static List<Grd> getGrdList(int isFinish) {
        return getGrdList(CurrentUser.getSchoolCode(), isFinish);
    }

    /**
     * 1.2 学校年级主任
     *
     * @param grdCodes 年级codes
     * @return List<GrdBoss>
     */
    public static List<GrdBoss> getGrdBossList(String schCode, String grdCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", schCode);
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("grd_codes", grdCodes);
        }};
        GxRequest request = new GxRequest("grdBoss", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(GrdBoss.class) : new ArrayList<>();
    }
    public static List<GrdBoss> getGrdBossList(String grdCodes) {
        return getGrdBossList(CurrentUser.getSchoolCode(), grdCodes);
    }

    /**
     * 1.3 学校班级
     * @param schCode 学校code
     * @param isFinish 是否毕业 0未毕业,1已毕业,-1全部
     * @return List<Cls>
     */
    public static List<Cls> getClsList(String schCode, int isFinish) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", schCode);
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("is_finish", isFinish);
        }};
        GxRequest request = new GxRequest("cls", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Cls.class) : new ArrayList<>();
    }
    public static List<Cls> getClsList(int isFinish) {
        return getClsList(CurrentUser.getSchoolCode(), isFinish);
    }
    public static List<Cls> getClsList(String grdCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("grd_codes", grdCodes);
        }};
        GxRequest request = new GxRequest("cls", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Cls.class) : new ArrayList<>();
    }

    /**
     * 1.4 学校班级任课老师
     *
     * @param clsCodes 班级codes
     * @return List<Tec>
     */
    public static List<ClsTec> getClsTecList(String clsCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("cls_codes", clsCodes);
        }};
        GxRequest request = new GxRequest("tec", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(ClsTec.class) : new ArrayList<>();

    }

    /**
     * 1.5 学校班级学生
     *
     * @param clsCodes 班级codes
     * @return List<Stu>
     */
    public static List<Stu> getStuList(String schCode, String clsCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("sch_code", schCode);
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("cls_codes", clsCodes);
        }};
        GxRequest request = new GxRequest("stu", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Stu.class) : new ArrayList<>();
    }
    public static List<Stu> getStuList(String clsCodes) {
        return getStuList(CurrentUser.getSchoolCode(), clsCodes);
    }

    /**
     * 1.6 学校班级学生统计
     *
     * @return List<Cls>
     */
    public static List<Cls> getClsStucList() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("cls/stuc", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Cls.class) : new ArrayList<>();
    }

    /**
     * 1.7 学校开设科目
     *
     * @return List<Sub>
     */
    public static List<Sub> getSubList() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("sub", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Sub.class) : new ArrayList<>();
    }

    /**
     * 1.8 科目组长信息
     *
     * @param subCodes 科目codes
     * @return List<SubBoss>
     */
    public static List<SubBoss> getSubBossList(String subCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("sub_codes", subCodes);
        }};
        GxRequest request = new GxRequest("subBoss", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(SubBoss.class) : new ArrayList<>();
    }

    /**
     * 1.9 学校部门
     *
     * @return List<Dpt>
     */
    public static List<Dpt> getDptList() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("dpt", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Dpt.class) : new ArrayList<>();
    }

    /**
     * 1.10 获取部门下用户信息
     *
     * @param dptCodes 部门codes
     * @param uidStat  是否有账号
     * @return List<JsyUser>
     */
    public static List<JsyUser> getJsyUserList(String dptCodes, int uidStat) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("dpt_codes", dptCodes);
            put("uid_stat", uidStat);
        }};
        GxRequest request = new GxRequest("dptUser", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(JsyUser.class) : new ArrayList<>();
    }

    /**
     * 1.11 按钮操作数据范围查询（供子系统调用）
     *
     * @param opCode  操作码
     * @param grdCode 年级id
     * @param clsCode 班级id
     * @param getGrd  是否获取年级
     * @param getCls  是否获取班级
     * @param getSub  是否获取科目
     * @param getStu  是否获取学生
     * @return List<DataObj>
     */
    private static JSONObject getDataRangeList(String opCode, String grdCode, String clsCode, boolean getGrd, boolean getCls, boolean getSub, boolean getStu) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", Optional.ofNullable(CurrentUser.getIndexCode()).orElse(""));
            put("access_token", CurrentUser.getAccessToken());

            put("op_code", opCode);

            put("get_grd", getGrd);
            put("all_grd", true);
            put("get_cls", getCls);
            put("all_cls", true);
            put("get_sub", getSub);
            put("all_sub", true);
            put("get_stu", getStu);
            put("all_stu", true);

            put("grd_code", grdCode);
            put("cls_code", clsCode);
        }};
        GxRequest request = new GxRequest("acl/dataRange", paraMap);
        GxResponse response = client.getResponse(request);
        if (response.isOk() && response.getData() != null) {
            return JSONObject.parseObject(response.getData());
        }
        return new JSONObject();
    }
    // 获取年级
    public static List<Grd> getGrdDataRangeList(String opCode) {
        JSONObject json = getDataRangeList(opCode, null, null, true, false, false, false);
        List<JSONObject> list = json.getJSONArray("grd_list").toJavaList(JSONObject.class);
        return list.stream().map(e -> {
            Grd o = new Grd();
            o.setGrdCode(e.getString("value"));
            o.setGrdName(e.getString("name"));
            return o;
        }).collect(Collectors.toList());
    }
    // 获取班级
    public static List<Cls> getClsDataRangeList(String opCode, String grdCode) {
        JSONObject json = getDataRangeList(opCode, grdCode, null, false, true, false, false);
        List<JSONObject> list = json.getJSONArray("cls_list").toJavaList(JSONObject.class);
        return list.stream().map(e -> {
            Cls o = new Cls();
            o.setClsCode(e.getString("value"));
            o.setClsName(e.getString("name"));
            return o;
        }).collect(Collectors.toList());
    }
    // 获取科目
    public static List<Sub> getSubDataRangeList(String opCode, String grdCode, String clsCode) {
        JSONObject json = getDataRangeList(opCode, grdCode, clsCode, false, false, true, false);
        List<JSONObject> list = json.getJSONArray("sub_list").toJavaList(JSONObject.class);
        return list.stream().map(e -> {
            Sub o = new Sub();
            o.setSubCode(e.getString("value"));
            o.setSubName(e.getString("name"));
            return o;
        }).collect(Collectors.toList());
    }
    // 获取学生
    public static List<Stu> getStuDataRangeList(String opCode, String grdCode, String clsCode) {
        JSONObject json = getDataRangeList(opCode, grdCode, clsCode, false, false, false, true);
        List<JSONObject> list = json.getJSONArray("stu_list").toJavaList(JSONObject.class);
        return list.stream().map(e -> {
            Stu o = new Stu();
            o.setStuCode(e.getString("value"));
            o.setStuName(e.getString("name"));
            return o;
        }).collect(Collectors.toList());
    }

    /**
     * 1.16 学校分科信息
     *
     * @return List<Art>
     */
    public static List<Art> getArt() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("art", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Art.class) : new ArrayList<>();
    }

    /**
     * 1.17 获取学校信息
     *
     * @param platformCode 平台代码， 非必需
     * @param areaCode     地区id
     * @return List<School>
     */
    public static List<School> getSchList(String platformCode, String areaCode) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", platformCode);
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("area_code", areaCode);
        }};
        GxRequest request = new GxRequest("sch", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(School.class) : new ArrayList<>();
    }

    /**
     * 1.18 获取学年
     *
     * @return List<Year>
     */
    public static List<Year> getYearList() {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("year", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Year.class) : new ArrayList<>();
    }

    /**
     * 1.19 根据学生code获取家长信息
     *
     * @param schCode  学校code
     * @param stuCodes 学生code
     * @return 家长列表
     */
    public static List<Par> getParList(String schCode, String stuCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", schCode);
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("stu_codes", stuCodes);
        }};
        GxRequest request = new GxRequest("parent", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(Par.class) : new ArrayList<>();
    }

    /**
     * 1.20 获取用户手机号码
     */
    public static List<JsyUser> getUserPhone(String userCodes) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("user_codes", userCodes);
        }};
        GxRequest request = new GxRequest("user/getPhone", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(JsyUser.class) : new ArrayList<>();
    }

    /**
     * 获取假期和补班日期
     * @param dayType
     * @return
     */
    public static List<String> getDays(int dayType) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());
            put("day_type", dayType);
        }};
        GxRequest request = new GxRequest("vacation/getDays", paraMap);
        GxResponse response = client.getResponse(request);
        return response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getJSONArray("list").toJavaList(String.class) : new ArrayList<>();
    }


}
