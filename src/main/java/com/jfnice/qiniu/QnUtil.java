package com.jfnice.qiniu;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;

/**
 * 七牛工具类
 */
public class QnUtil {

    /**
     * 上传本地文件
     *
     * @param localFilePath 文件物理地址
     * @param key           七牛保存的key
     * @return key（七牛保存的key）
     */
    public static String upload(String localFilePath, String key) {
        try {
            UploadManager uploadManager = new UploadManager(new Configuration());
            String resData = Api.getUploadToken(key);
            if (resData != null) {
                JSONObject resDataObject = JSONObject.parseObject(resData);
                Response res = uploadManager.put(localFilePath, resDataObject.getString("Key"), resDataObject.getString("Token"));
                if (res.statusCode == 200) {
                    return resDataObject.getString("Key");
                } else {
                    LogKit.error("上传失败，" + localFilePath);
                    return null;
                }
            } else {
                return null;
            }
        } catch (QiniuException e) {
            return null;
        }
    }

}
