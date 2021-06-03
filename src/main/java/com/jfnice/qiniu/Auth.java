package com.jfnice.qiniu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Auth {

    public static Resp request(String Api, Map<String, String> paraMap) {
        Resp resp;
        HttpURLConnection conn = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        try {
            conn = (HttpURLConnection) new URL(Api).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000); //连接主机超时（单位：毫秒）
            conn.setReadTimeout(10000); //从主机读取数据超时（单位：毫秒）
            out = new DataOutputStream(conn.getOutputStream());

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }

            out.write(sb.substring(1).getBytes(StandardCharsets.UTF_8));
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb.delete(0, sb.length());
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            resp = new Resp(jsonObject.get("Status").toString(), jsonObject.get("Message").toString(), jsonObject.get("Data").toString());
        } catch (SocketTimeoutException e) {
            return new Resp("0", "接口连接超时", null);
        } catch (Exception e) {
            return new Resp("0", e.getMessage(), null);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return resp;
    }

}
