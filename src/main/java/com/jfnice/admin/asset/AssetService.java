package com.jfnice.admin.asset;

import com.google.common.base.Strings;
import com.jfinal.core.JFinal;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfnice.commons.CacheName;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.model.Asset;
import com.jfnice.qiniu.QnApi;
import com.jfnice.qiniu.QnUtil;
import com.school.library.constants.SysConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class AssetService extends JFniceBaseService<Asset> {

    public static final String BASE_UPLOAD_PATH;  //上传根路径
    public static final int UPLOAD_MAX_SIZE = 2097152; //上传大小限制，单位byte
    /**
     * 默认自定义的二级目录
     */
    public static final String SECOND_DIR = "sys";
    //文件后缀限制
    private static final Set<String> supportExtSet = new HashSet<String>(Arrays.asList(
            "jpg", "jpeg", "png", "gif",
            "mp3", "wma", "wav",
            "mp4", "avi", "wmv", "rm", "rmvb",
            "txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "rar"
    ));

    static {
        String path = JFinal.me().getConstants().getBaseUploadPath();
        if (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        BASE_UPLOAD_PATH = path;
    }

    /**
     * 文件另存到指定位置
     *
     * @param url 上传响应信息中的资源url
     * @param dir 相对于JFniceMainConfig中配置的BaseUploadPath的路径，如：/image/stu/ 或 /file（ 不可使用返回上层目录的路径，如： ../../image ）
     * @return url 返回新的url
     */
    public String saveAs(String url, String dir) {
        if (StrKit.isBlank(url)) {
            return null;
        }

        if (StrKit.isBlank(dir)) {
            throw new RuntimeException("必须指定dir参数！");
        }

        if (dir.matches("^.*\\.+.*$")) {
            throw new RuntimeException("dir参数不可包含“.”符号！");
        }

        if (!dir.startsWith("/") && !dir.startsWith("\\")) {
            dir = "/".concat(dir);
        }

        if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            dir = dir.concat("/");
        }

        Asset asset = getAssetByUrl(url);
        String day = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File directory = new File(BASE_UPLOAD_PATH + dir + day);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = asset.getId() + "." + asset.getExt();
        String oldFilePath = BASE_UPLOAD_PATH + asset.getUrl();
        String newFilePath = directory.getPath() + File.separator + fileName;
        new File(oldFilePath).renameTo(new File(newFilePath));

        //由于以上文件无论外层事务是否发生回滚，文件路径均已改变
        //需另起新线程脱离外层事务同步更新路径，避免异常回滚后产生无法定时自动删除的垃圾文件
        String newUrl = dir + day + "/" + fileName;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                asset.setUrl(newUrl);
                update(asset);
            }
        });

        try {
            thread.start();//执行子线程
            thread.join();//等待子线程执行完成

            //设置保存状态（外层事务发生异常会回滚）
            asset.setSaveTime(new Date());
            asset.setSaved(true);
            update(asset);

            //由于外层事务可能发生回滚，导致save_time和saved数值不确定，需清除后再缓存
            asset.remove("save_time", "saved");
            JsyCacheKit.put(CacheName.ASSET, asset.getId(), asset);
            JsyCacheKit.put(CacheName.ASSET, asset.getUrl(), asset);
            return asset.getUrl();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * 保存到七牛
     *
     * @param url
     * @param dir
     * @return
     */
    public String saveToQn(String url, String dir) {
        if (StrKit.isBlank(url)) {
            return null;
        }

        if (StrKit.isBlank(dir)) {
            throw new RuntimeException("必须指定dir参数！");
        }

        if (dir.matches("^.*\\.+.*$")) {
            throw new RuntimeException("dir参数不可包含“.”符号！");
        }

        if (dir.startsWith("/") || dir.startsWith("\\")) {
            dir = dir.substring(1);
        }

        if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            dir = dir.concat("/");
        }

        String day = new SimpleDateFormat("yyyyMMdd").format(new Date());

        if (!url.startsWith("/temp/")) {
            return url;
        }

        String fileName = url.replace("/temp", "");

        String localFilePath = BASE_UPLOAD_PATH + url;
        return QnUtil.upload(localFilePath, dir + day + fileName);
    }

    /**
     * 删除文件
     */
    public void delete(String url) {
        Db.update(Db.getSql("Asset.deleteByUrl"), url);
        FileKit.delete(new File(BASE_UPLOAD_PATH + url));
        Asset asset = JsyCacheKit.get(CacheName.ASSET, url);
        if (asset != null) {
            JsyCacheKit.remove(CacheName.ASSET, asset.getId());
            JsyCacheKit.remove(CacheName.ASSET, asset.getUrl());
        }
    }

    /**
     * 根据id获取asset
     */
    public Asset getAssetById(String id) {
        Asset asset = JsyCacheKit.get(CacheName.ASSET, id);
        if (asset == null) {
            asset = queryById(id);
            if (asset == null) {
                throw new ErrorMsg("文件异常，请重新上传！");
            }
            JsyCacheKit.put(CacheName.ASSET, asset.getId(), asset);
            JsyCacheKit.put(CacheName.ASSET, asset.getUrl(), asset);
        }
        return asset;
    }

    /**
     * 根据url获取asset
     */
    public Asset getAssetByUrl(String url) {
        Asset asset = JsyCacheKit.get(CacheName.ASSET, url);
        if (asset == null) {
            asset = queryByUrl(url);
            if (asset == null) {
                throw new ErrorMsg("文件异常，请重新上传！");
            }

            JsyCacheKit.put(CacheName.ASSET, asset.getId(), asset);
            JsyCacheKit.put(CacheName.ASSET, asset.getUrl(), asset);
        }
        return asset;
    }

    public void validateFileExt(Asset asset) {
        if (!supportExtSet.contains(asset.getExt())) {
            FileKit.delete(new File(BASE_UPLOAD_PATH + asset.getUrl()));
            throw new ErrorMsg("不支持" + asset.getExt() + "类型文件上传");
        }
        if (asset.getSize() > UPLOAD_MAX_SIZE) {
            FileKit.delete(new File(BASE_UPLOAD_PATH + asset.getUrl()));
            throw new ErrorMsg(asset.getName() + "文件大小" + asset.getSize() + "超过最大限制" + UPLOAD_MAX_SIZE);
        }
    }

    public Asset queryByUrl(String url) {
        return Asset.dao.findFirst(Db.getSql("Asset.queryByUrl"), url);
    }

    public void batchSave(List<Asset> assetList) {
        assetList.stream().forEach(a -> {
            String schoolCode = CurrentUser.getSchoolCode();
            if(Strings.isNullOrEmpty(schoolCode)){
                schoolCode = SECOND_DIR;
            }
            a.setUrl(QnApi.getPublicLink(this.saveToQn( a.getUrl(), SysConstants.BOOK_UPLOAD_QN_FILE_DIR + "/" + schoolCode)));
        });
        Db.batchSave(assetList, assetList.size());
        for (Asset asset : assetList) {
            if (asset.getSize() > UPLOAD_MAX_SIZE) {
                FileKit.delete(new File(BASE_UPLOAD_PATH + asset.getUrl()));
                throw new ErrorMsg(asset.getName() + "文件大小" + asset.getSize() + "超过最大限制" + UPLOAD_MAX_SIZE);
            }

            validateFileExt(asset);
            JsyCacheKit.put(CacheName.ASSET, asset.getId(), asset);
            JsyCacheKit.put(CacheName.ASSET, asset.getUrl(), asset);
        }
    }

    public void clear() {
        FileKit.delete(new File(BASE_UPLOAD_PATH + "/temp"));
        List<Asset> assetList = Asset.dao.find(Db.getSql("Asset.queryInvalidAssetList"));
        for (Asset asset : assetList) {
            delete(asset.getUrl());
        }
        Db.update(Db.getSql("Asset.clear"));
        JsyCacheKit.removeAll(CacheName.ASSET);
    }

}