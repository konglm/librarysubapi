package com.school.api.gx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfnice.admin.pub.PublicService;
import com.jfnice.commons.Config;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.utils.ThreadLocalUtil;
import com.school.api.model.Login;
import com.school.api.model.LoginUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("serial")
public class PtApi {

    private static final GxClient skinClient = new GxClient(Config.PT_SKIN_API_URL);
    private static final GxClient subClient = new GxClient(Config.PT_SUB_API_URL);
    private static PublicService publicService = Aop.get(PublicService.class);

    /**
     * 登录
     *
     * @param platformCode 平台代码
     * @param appCode      应用系统前缀
     * @param loginName    登录名
     * @param password     MD5加密过的密码
     * @return Login
     */
    public static Login login(String platformCode, String appCode, String loginName, String password) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", platformCode);
            put("app_code", appCode);
            put("uuid", "");
            put("webid", "");
            put("login_name", loginName);
            put("password", password);
        }};
        GxRequest request = new GxRequest("login", paraMap);
        GxResponse response = skinClient.getResponse(request);
        return response.isOk() && response.getData() != null ? JSON.parseObject(response.getData(), Login.class) : new Login();
    }
    /**
     * 游客登录
     */
    public static void touristLogin() {
        String loginName = PropKit.get("touristLoginName").trim();
        String password = HashKit.md5(publicService.getEncryptPrefix() + PropKit.get("touristPassword").trim());
        Login login = JsyCacheKit.get("touristLoginUser", loginName);
        LoginUser loginUser = new LoginUser();
        boolean refreshLogin = login == null || login.getAccessToken() == null;
        if (!refreshLogin) {
            try {
                loginUser = getCurrentUserInfo(login.getAccessToken(), Config.PLATFORM_CODE, "", "", "");
            } catch (ErrorMsg e) {
                refreshLogin = true;
            }
        }
        if (refreshLogin) {
            login = PtApi.login(Config.PLATFORM_CODE, "", loginName, password);
            JsyCacheKit.put("touristLoginUser", loginName, login);
            loginUser = getCurrentUserInfo(login.getAccessToken(), Config.PLATFORM_CODE, "", "", "");
        }
        loginUser.setAccessToken(login.getAccessToken());
        ThreadLocalUtil.getInstance().bind(loginUser);
    }

    /**
     * 3.1:获取当前登录用户信息（供子系统后台调用）
     */
    public static LoginUser getCurrentUserInfo(String token, String platformCode, String appCode, String schCode, String indexCode) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", platformCode);
            put("app_code", appCode);
            put("unit_code", schCode);
            put("index_code", indexCode);
            put("access_token", token);
        }};
        GxRequest request = new GxRequest("user/currentUserInfo", paraMap);
        GxResponse response = subClient.getResponse(request);
        return response.isOk() && response.getData() != null ? JSON.parseObject(response.getData(), LoginUser.class) : new LoginUser();
    }

    /**
     * 3.3:根据选择的年级班级科目查询权限符（前端调用，判断按钮是否显示，供子系统调用）
     *
     * @param opCodes 操作码：多个使用,分割
     * @param grdCode 年级id
     * @param clsCode 班级id
     * @param subCode 科目code
     * @param stuCode 学生id
     * @return 权限符集合
     */
    public static String permissionByPosition(String opCodes, Object grdCode, Object clsCode, Object subCode, Object stuCode) {
        Map<String, Object> paraMap = new HashMap<String, Object>() {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", CurrentUser.getIndexCode());
            put("access_token", CurrentUser.getAccessToken());

            put("op_code", opCodes == null ? "" : opCodes);
            put("grd_code", grdCode);
            put("cls_code", clsCode);
            put("sub_code", subCode);
            put("stu_code", stuCode);
        }};
        String res;
        GxRequest request = new GxRequest("acl/permissionByPosition", paraMap);
        GxResponse response = subClient.getResponse(request);
        res = response.isOk() && response.getData() != null ? JSONObject.parseObject(response.getData()).getString("result") : "";
        return res;
    }
    public static String getPermissionByPositionList(OpCodeEnum[] paras, Object grdCode, Object clsCode, Object subCode, Object stuCode) {
        if (paras == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (OpCodeEnum p : paras) {
            sb.append(",").append(p.getV());
        }
        String opCodes = sb.length() > 0 ? sb.substring(1) : "";
        return permissionByPosition(opCodes, grdCode, clsCode, subCode, stuCode);
    }
    public static String getPermissionByPositionList(OpCodeEnum[] paras) {
        return getPermissionByPositionList(paras, "0", "0", "0", "0");
    }

    /**
     * 验证token的有效性
     *
     * @return boolean
     */
    public static boolean verifyToken() {
        Map<String, Object> paraMap = new HashMap<String, Object>(16) {{
            put("platform_code", CurrentUser.getPlatformCode());
            put("app_code", CurrentUser.getAppCode());
            put("unit_code", CurrentUser.getSchoolCode());
            put("index_code", Optional.ofNullable(CurrentUser.getIndexCode()).orElse(""));
            put("access_token", CurrentUser.getAccessToken());
        }};
        GxRequest request = new GxRequest("token/verify", paraMap);
        GxResponse response = subClient.getResponse(request);
        return response.isOk();
    }

}