package com.jfnice.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.ext.StaticRoute;
import com.jfnice.handler.MultipartRequestExtend;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class JFniceBaseController extends Controller {

    private final JFniceIBaseRoutes IRoutes = (JFniceIBaseRoutes) StaticRoute.getRoutes(getClass());

    /**
     * 获取主机url
     */
    @NotAction
    public String getHost() {
        return getRequest().getScheme() + "://" + getRequest().getServerName() + ":" + getRequest().getServerPort();
    }

    /**
     * 获取上下文
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getContextPath() {
        return getRequest().getContextPath();
    }

    /**
     * 获取当前项目路由的baseViewPath
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getBaseViewPath() {
        if (null == IRoutes) {
            return "";
        }

        String baseViewPath = IRoutes.getBaseViewPath();
        if (null == baseViewPath) {
            return "/";
        }
        return baseViewPath;
    }

    /**
     * 获取静态文件放置路径
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getStaticPath() {
        if (null == IRoutes) {
            return "";
        }

        String staticPath = IRoutes.getStaticPath();
        if (null == staticPath || "/".equals(staticPath)) {
            return "";
        } else if (staticPath.endsWith("/")) {
            return staticPath.substring(0, staticPath.length() - 1);
        } else {
            return staticPath;
        }
    }

    /**
     * 获取当前项目名称
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getProjectName() {
        if (null == IRoutes) {
            return "";
        }

        String projectName = IRoutes.getProjectName();
        if (null == projectName) {
            return "";
        }

        return projectName;
    }

    /**
     * 获取当前项目根路由
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getProjectUrl() {
        if (null == IRoutes) {
            return "";
        }

        String projectUrl = IRoutes.getProjectUrl();
        if (null == projectUrl || "/".equals(projectUrl)) {
            return "";
        } else if (projectUrl.endsWith("/")) {
            return projectUrl.substring(0, projectUrl.length() - 1);
        } else {
            return projectUrl;
        }
    }

    /**
     * 获取当前控制器名称
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getControllerName() {
        return getClass().getSimpleName().replace("Controller", "");
    }

    /**
     * 获取当前控制器根路由
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getControllerUrl() {
        String controllerUrl = StaticRoute.getControllerKey(getClass());
        if (null == controllerUrl) {
            return "";
        }

        if (controllerUrl.endsWith("/")) {
            controllerUrl = controllerUrl.substring(0, controllerUrl.length() - 1);
        }
        return controllerUrl;
    }

    /**
     * 获取当前项目入口actionUrl，用于shiro会话超时或未登录跳转
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getEntryUrl() {
        if (null == IRoutes) {
            return null;
        }

        String entryUrl = IRoutes.getEntryUrl();
        if (entryUrl != null && StaticRoute.getAction(entryUrl) == null) {
            fail("【主路由" + IRoutes.getClass().getSimpleName() + "】entryUrl配置有误！（entryUrl: " + entryUrl + "）");
        }

        return entryUrl != null ? entryUrl : "/";
    }

    /**
     * 获取当前项目错误页面统一处理actionUrl
     *
     * @return String
     * @author JFnice
     */
    @NotAction
    public String getErrorUrl() {
        if (null == IRoutes) {
            return null;
        }

        String errorUrl = IRoutes.getErrorUrl();
        if (errorUrl != null && StaticRoute.getAction(errorUrl) == null) {
            fail("【主路由" + IRoutes.getClass().getSimpleName() + "】errorUrl配置有误！（errorUrl: " + errorUrl + "）");
        }
        return errorUrl;
    }

    /**
     * 跳转错误页面
     *
     * @param code 错误代码
     * @param msg  错误提示
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void showErrorView(String code, String msg) {
        String errorUrl = getErrorUrl();
        if (errorUrl != null) {
            setAttr("code", code);
            setAttr("msg", msg);
            forwardAction(errorUrl);
        } else {
            fail(msg, code);
        }
    }

    /**
     * 是否为POST提交方式
     *
     * @return boolean
     * @author JFnice
     */
    @NotAction
    public boolean isPost() {
        return "POST".equalsIgnoreCase(getRequest().getMethod());
    }

    /**
     * 是否为AJAX请求方式
     *
     * @return boolean
     * @author JFnice
     */
    @NotAction
    public boolean isAjax() {
        String XRequestedWith = getRequest().getHeader("X-Requested-With");
        return XRequestedWith != null && "XMLHttpRequest".equalsIgnoreCase(XRequestedWith);
    }

    /**
     * 是否为multipart/form-data请求
     *
     * @return boolean
     * @author JFnice
     */
    @NotAction
    public boolean isMultipart() {
        String contentType = getRequest().getContentType();
        return contentType != null && contentType.toLowerCase().indexOf("multipart/form-data") != -1;
    }

    /**
     * 返回前端JSON成功数据
     *
     * @param msg 成功提示
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void ok(String msg) {
        ok(msg, ResultEnum.SUCCESS.getCode());
    }

    @NotAction
    public void ok(Object data) {
        ok(ResultEnum.SUCCESS.getMsg(), ResultEnum.SUCCESS.getCode(), data == null ? new JSONObject() : data);
    }

    /**
     * 返回前端JSON成功数据
     *
     * @param msg  成功提示
     * @param code 成功代码
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void ok(String msg, String code) {
        ok(msg, code, null);
    }

    @NotAction
    public void ok(String msg, Object data) {
        ok(msg, ResultEnum.SUCCESS.getCode(), data);
    }

    /**
     * 返回前端JSON成功数据
     *
     * @param msg  成功提示
     * @param code 成功代码
     * @param data 附加数据
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void ok(String msg, String code, Object data) {
        ajaxMsg(true, msg, code, data);
    }

    /**
     * 返回前端JSON成功数据
     *
     * @param isOk 决定JSON中的status
     * @param msg  成功提示
     * @param code 成功代码
     * @param data 附加数据
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void ajaxMsg(boolean isOk, String msg, String code, Object data) {
        Ret ret = Ret.by("msg", msg)
                .set("code", code)
                .set("data", data);
        renderJson(isOk ? ret.setOk() : ret.setFail());
    }

    /**
     * 返回前端JSON错误数据
     *
     * @param msg 错误提示
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void fail(String msg) {
        throw new ErrorMsg(msg);
    }

    /**
     * 返回前端JSON错误数据
     *
     * @param msg  错误提示
     * @param code 错误代码
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void fail(String msg, String code) {
        throw new ErrorMsg(msg, code);
    }

    /**
     * 返回前端JSON错误数据
     *
     * @param msg  错误提示
     * @param code 错误代码
     * @param data 附加数据
     * @return 无
     * @author JFnice
     */
    @NotAction
    public void fail(String msg, String code, Object data) {
        throw new ErrorMsg(msg, code, data);
    }

    /* XssRequestHander transfer START */
    @NotAction
    public List<UploadFile> getFiles(String uploadPath, Integer maxPostSize, String encoding) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartRequestExtend == false)
            request = new MultipartRequestExtend(request, uploadPath, maxPostSize, encoding);
        return ((MultipartRequestExtend) request).getFiles();
    }

    @NotAction
    public UploadFile getFile(String parameterName, String uploadPath, Integer maxPostSize, String encoding) {
        getFiles(uploadPath, maxPostSize, encoding);
        return getFile(parameterName);
    }

    @NotAction
    public List<UploadFile> getFiles(String uploadPath, int maxPostSize) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartRequestExtend == false)
            request = new MultipartRequestExtend(request, uploadPath, maxPostSize);
        return ((MultipartRequestExtend) request).getFiles();
    }

    @NotAction
    public UploadFile getFile(String parameterName, String uploadPath, int maxPostSize) {
        getFiles(uploadPath, maxPostSize);
        return getFile(parameterName);
    }

    @NotAction
    public List<UploadFile> getFiles(String uploadPath) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartRequestExtend == false)
            request = new MultipartRequestExtend(request, uploadPath);
        return ((MultipartRequestExtend) request).getFiles();
    }

    @NotAction
    public UploadFile getFile(String parameterName, String uploadPath) {
        getFiles(uploadPath);
        return getFile(parameterName);
    }

    @NotAction
    public List<UploadFile> getFiles() {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartRequestExtend == false)
            request = new MultipartRequestExtend(request);
        return ((MultipartRequestExtend) request).getFiles();
    }

    @NotAction
    public UploadFile getFile() {
        List<UploadFile> uploadFiles = getFiles();
        return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
    }

    @NotAction
    public UploadFile getFile(String parameterName) {
        List<UploadFile> uploadFiles = getFiles();
        for (UploadFile uploadFile : uploadFiles) {
            if (uploadFile.getParameterName().equals(parameterName)) {
                return uploadFile;
            }
        }
        return null;
    }
    /* XssRequestHander transfer END */

}