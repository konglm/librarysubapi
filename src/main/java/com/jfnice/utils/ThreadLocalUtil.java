package com.jfnice.utils;

import com.school.api.model.LoginUser;

/**
 * 线程工具类：将用户信息绑定到当前线程
 *
 * @author jsy
 */
public class ThreadLocalUtil {

    private static final ThreadLocalUtil INSTANCE = new ThreadLocalUtil();
    private final InheritableThreadLocal<LoginUser> userInfoThreadLocal = new InheritableThreadLocal<>();

    /**
     * 私有化构造
     */
    private ThreadLocalUtil() {
    }

    /**
     * 获取单例
     *
     * @return ThreadLocalUtil
     */
    public static ThreadLocalUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 将用户对象绑定到当前线程中，键为userInfoThreadLocal对象，值为userInfo对象
     */
    public void bind(LoginUser loginUser) {
        userInfoThreadLocal.set(loginUser);
    }

    /**
     * 得到绑定的用户对象
     */
    public LoginUser getLoginUser() {
        return userInfoThreadLocal.get();
    }

    /**
     * 移除绑定的用户对象
     */
    public void remove() {
        userInfoThreadLocal.remove();
    }

}
