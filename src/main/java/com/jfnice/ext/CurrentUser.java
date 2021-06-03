package com.jfnice.ext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfnice.utils.ThreadLocalUtil;
import com.school.api.gx.PtApi;
import com.school.api.model.LoginUser;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 当前用户信息
 *
 * @author jsy
 */
public class CurrentUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String getAccessToken() {
        return getLoginUser().getAccessToken();
    }

    public static String getPlatformCode() {
        return getLoginUser().getPlatformCode();
    }

    public static String getAppCode() {
        return getLoginUser().getAppCode();
    }

    public static Object getIndexCode() {
        return getLoginUser().getIndexCode();
    }

    public static String getSchoolCode() {
        return getLoginUser().getSchoolCode();
    }

    public static String getPlatformName() {
        return (String) getAttr(getLoginUser(), "platform_name");
    }

    public static String getSchoolName() {
        return (String) getAttr(getLoginUser(), "unit_name");
    }

    public static String getUserCode() {
        return (String) getAttr(getLoginUser(), "user_code");
    }

    public static String getUserName() {
        return (String) getAttr(getLoginUser(), "user_name");
    }

    public static String getLoginName() {
        return (String) getAttr(getLoginUser(), "login_name");
    }

    public static String getImgUrl() {
        return (String) getAttr(getLoginUser(), "img_url");
    }

    public static String getTypeCode() {
        return (String) getAttr(getLoginUser(), "type_code");
    }

    public static Short getSex() {
        return (Short) getAttr(getLoginUser(), "sex");
    }

    public static Object getAttr(LoginUser loginUser, String key) {
        JSONObject json = (JSONObject) JSON.toJSON(loginUser);
        Object res = json.get(key);
        if (res == null) {
            updateUser(loginUser);
            json = (JSONObject) JSON.toJSON(loginUser);
            res = json.get(key);
        }
        return res;
    }

    public static void updateUser(LoginUser loginUser) {
        if (loginUser.getAccessToken() != null) {
            LoginUser newLoginUser = PtApi.getCurrentUserInfo(loginUser.getAccessToken(), loginUser.getPlatformCode(), loginUser.getAppCode(), loginUser.getSchoolCode(), loginUser.getIndexCode());
            try {
                Field[] fields = loginUser.getClass().getDeclaredFields();
                for (Field field : fields) {
                    // 获取原来的访问控制权限
                    boolean accessFlag = field.isAccessible();
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    if (field.get(loginUser) == null) {
                        field.set(loginUser, field.get(newLoginUser));
                    }
                    field.setAccessible(accessFlag);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static LoginUser getLoginUser() {
        return Optional.ofNullable(ThreadLocalUtil.getInstance().getLoginUser()).orElse(new LoginUser());
    }

}
