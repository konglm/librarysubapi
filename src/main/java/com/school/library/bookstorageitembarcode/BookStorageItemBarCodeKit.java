package com.school.library.bookstorageitembarcode;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.kit.DateKit;
import com.jfnice.commons.CacheName;
import com.jfnice.cache.JsyCacheKit;
import com.school.library.constants.RedisConstants;

import java.util.Date;

/**
 * @Description 条形码辅助类
 * @Author jsy
 * @Date 2020/3/18
 * @Version V1.0
 **/

public class BookStorageItemBarCodeKit {

    /**
     * 生成条形码
     * @return
     */
    public static String[] generateBarCode(String schoolCode, int count){
        String [] barCodes = new String[count];
        String lock = schoolCode.intern();
        StringBuilder codeBuild = new StringBuilder();
        synchronized(lock){
            String redisKey = RedisConstants.BAR_CODE_SEQ_KEY_PREFIX + schoolCode;
            Integer seq = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, redisKey);
            int i = 0;
            while(i < count){
                seq = seq == null ? 1 : (seq + 1) ;
                codeBuild.append(DateKit.toStr(new Date(), "yyMMdd"));
                codeBuild.append(String.format("%06d",seq));
                barCodes[i] = codeBuild.toString();
                codeBuild = new StringBuilder();
                i++;
            }

            JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, seq, RedisConstants.TIME_TO_LIVE_SECONDS);
        }
        return barCodes;
    }

    /**
     * 生成索书号
     * 存储的格式{catalogNo:{"count": 4, yu6772323(bookOrder): 3, y870lsfa: 2, 98yhlsdfa: 1} }
     * @param schoolCode
     * @param catalogNo
     * @param bookOrder 书名+作者+出版社+出版日期等组成的MD5签名
     * @return
     */
    public static String generateCheckNo(String schoolCode, String catalogNo, String bookOrder){
        String key = RedisConstants.CHECK_NO_COUNT_KEY_PREFIX + schoolCode;
        JSONObject catJson = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, key);
        JSONObject json = null;
        Integer count = null;
        if(null != catJson){
            json = catJson.getJSONObject(catalogNo);
            if(null != json){
                count = json.getInteger("count");
                if(null!= json.getInteger(bookOrder)){
                    return catalogNo + "/" + json.getInteger(bookOrder);
                }
            }else{
                json = new JSONObject();
            }
        }else{
            catJson = new JSONObject();
            json = new JSONObject();
        }

        count = null == count ? 1 : count + 1;
        json.put("count", count);
        json.put(bookOrder, count);
        catJson.put(catalogNo, json);

        JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, key, catJson);
        return catalogNo + "/" + count;
    }

}
