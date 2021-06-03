package com.jfnice.admin.asset;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfnice.annotation.ShiroClear;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.ResultEnum;
import com.jfnice.interceptor.ApiAuthInterceptor;
import com.jfnice.interceptor.ApiSignInterceptor;
import com.jfnice.kit.UrlKit;
import com.jfnice.model.Asset;

/**
 * 上传附件
 */
@ShiroClear
@Before(AssetValidator.class)
@Clear(value = {ApiSignInterceptor.class, ApiAuthInterceptor.class})
public class AssetController extends JFniceBaseController {

    @Inject
    private AssetService assetService;

    /**
     * 两种方式可以通过上传安全校验
     * 方式①（推荐）：url带参数csrfToken，如：#(CONTEXT_PATH)/Asset/upload?csrfToken=#(CSRF_TOKEN)
     * 方式②（不推荐）：request请求头部header带Csrf-Token，不推荐原因：部分浏览器的不支持header参数
     */
    public void upload() {
        Asset asset = assetService.getAssetById(getFile().getFileName());
        ok("上传成功！", ResultEnum.SUCCESS.getCode(), asset);
    }

    /**
     * 示例：
     * 1、 后台渲染用：<a href="#(CONTEXT_PATH)/Asset/download?url=#(encodeUrl(encodeUrl(attachment.url)))&name=#(encodeUrl(encodeUrl(attachment.name)))" target="_blank">下载</a>
     * 2、前台js拼接用： encodeURIComponent(encodeURIComponent(url))
     */
    public void download() {
        String url = UrlKit.decodeUrl(getPara("url"));
        String name = UrlKit.decodeUrl(getPara("name"));
        renderFile(url, name);
    }

}
