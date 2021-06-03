package com.jfnice.handler;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.handler.Handler;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.ExceededSizeException;
import com.jfinal.upload.UploadFile;
import com.jfnice.admin.asset.AssetService;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.Asset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 扩展成可修改parameter的request
 */
public class XssRequestExtendHandler extends Handler {

    private static final AssetService assetService = Aop.get(AssetService.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (isMultipart(request)) {
            MultipartRequestExtend multiRequest = null;
            try {
                multiRequest = new MultipartRequestExtend(request, "temp");
                uploadFileHandle(multiRequest);
                next.handle(target, multiRequest, response, isHandled);
            } catch (ErrorMsg e) {
                JSONObject json = new JSONObject();
                json.put("msg", e.getMessage());
                json.put("code", e.getCode());
                json.put("state", "fail");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                response.setHeader("Access-Control-Allow-Origin", "*");//设置跨域
                //response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                //response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, csrf-token");
                //response.setHeader("Access-Control-Allow-Credentials", "true");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.print(json.toString());
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                isHandled[0] = true;
            }catch(ExceededSizeException ex){
                JSONObject json = new JSONObject();
                json.put("msg", "文件大小超出" + AssetService.UPLOAD_MAX_SIZE/1024/1000 + "M限制");
                json.put("code", "ERROR_FILE_MAX");
                json.put("state", "fail");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                response.setHeader("Access-Control-Allow-Origin", "*");//设置跨域
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.print(json.toString());
                    out.flush();
                    out.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                isHandled[0] = true;
                //next.handle(target, request, response, isHandled);
                //throw new ErrorMsg("文件大小超出" + AssetService.UPLOAD_MAX_SIZE/1024/1000 + "M限制");
            }
        } else {
            next.handle(target, request instanceof HttpServletRequestExtend ? request : new HttpServletRequestExtend(request), response, isHandled);
        }
    }

    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().contains("multipart/form-data");
    }

    private void uploadFileHandle(MultipartRequestExtend multiRequest) {
        List<UploadFile> uploadFiles = multiRequest.getFiles();
        if (uploadFiles != null) {
            UploadFile uploadFile = null;
            List<Asset> assetList = new ArrayList<Asset>();
            File file;
            for (int i = 0, len = uploadFiles.size(); i < len; i++) {
                uploadFile = uploadFiles.get(i);
                String id = StrKit.getRandomUUID();
                file = uploadFile.getFile();
                long size = file.length();
                String originalFileName = uploadFile.getOriginalFileName().toLowerCase();
                String ext = FileKit.getFileExtension(originalFileName);
                file.renameTo(new File(uploadFile.getUploadPath() + File.separator + id + "." + ext));
                uploadFiles.set(i, new UploadFile(uploadFile.getParameterName(), uploadFile.getUploadPath(), String.valueOf(id), originalFileName, uploadFile.getContentType()));

                Asset asset = new Asset();
                asset.setId(id);
                asset.setName(originalFileName);
                asset.setExt(ext);
                asset.setUrl("/temp/" + id + "." + ext);
                asset.setSize(size);
                asset.setType(uploadFile.getContentType());
                asset.setCreateTime(new Date());
                asset.setSaved(false);

                assetService.validateFileExt(asset);
                assetList.add(asset);
            }
            assetService.batchSave(assetList);
        }
    }

}
