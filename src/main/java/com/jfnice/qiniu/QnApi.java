package com.jfnice.qiniu;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfnice.cache.JsyCacheKit;

public class QnApi {

    private static final String cacheName = "qnLinkCache";
    private static final String PRIVATE_BUCKET_DOMAIN = PropKit.get("bucketDomain").trim();
    private static final int PRIVATE_ACCESS_TIME_OUT = 3210;//单位：秒, 必需大于10，具体参见方法：putCache

    /**
     * 获取私有思源link，需要加上token
     */
    public static String getLink(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        //带域名地址
        if (fileKey.startsWith(PRIVATE_BUCKET_DOMAIN) || fileKey.startsWith("http")) {
            return fileKey;
        }

        //本地资源
        if (fileKey.startsWith("/")) {
            return JFinal.me().getContextPath() + fileKey;
        }

        String link = JsyCacheKit.get(cacheName, fileKey);
        if (link == null) {
            link = Api.getLink(PRIVATE_BUCKET_DOMAIN + fileKey);
            JsyCacheKit.put(cacheName, fileKey, link, PRIVATE_ACCESS_TIME_OUT - 10);
        }
        return link;
    }

    /**
     * 获取公共的link
     */
    public static String getPublicLink(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        //带域名地址
        if (fileKey.startsWith(PRIVATE_BUCKET_DOMAIN) || fileKey.startsWith("http")) {
            return fileKey;
        }

        //本地资源
        if (fileKey.startsWith("/")) {
            return JFinal.me().getContextPath() + fileKey;
        }

        return PRIVATE_BUCKET_DOMAIN + fileKey;
    }

}
