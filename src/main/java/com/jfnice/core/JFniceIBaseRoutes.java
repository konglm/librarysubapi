package com.jfnice.core;

public interface JFniceIBaseRoutes {

    /**
     * 获取当前项目路由的baseViewPath
     *
     * @return String
     * @author JFnice
     */
    String getBaseViewPath();

    /**
     * 获取静态文件放置路径
     *
     * @return String
     * @author JFnice
     */
    String getStaticPath();

    /**
     * 获取当前项目名称
     *
     * @return String
     * @author JFnice
     */
    String getProjectName();

    /**
     * 获取当前项目根路由
     *
     * @return String
     * @author JFnice
     */
    String getProjectUrl();

    /**
     * 获取当前项目错误页面统一处理actionUrl
     *
     * @return String
     * @author JFnice
     */
    String getErrorUrl();

    /**
     * 获取当前项目入口actionUrl，用于shiro会话超时或未登录跳转
     *
     * @return String
     * @author JFnice
     */
    String getEntryUrl();

}
