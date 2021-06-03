package com.jfnice.handler;

import com.jfinal.core.JFinal;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequestExtend extends HttpServletRequestWrapper {

    private byte[] body = new byte[0];
    private Map<String, String[]> paraMap = new HashMap<>();

    public HttpServletRequestExtend(HttpServletRequest request) {
        super(request);
        paraMap.putAll(request.getParameterMap());
        if (request.getContentType() != null && !request.getContentType().contains("application/x-www-form-urlencoded")) {
            try {
                body = readBytes(request.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b)) != -1) {
            baos.write(b, 0, len);
        }
        return baos.toByteArray();
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), JFinal.me().getConstants().getEncoding()));
    }

    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    public void setParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                paraMap.put(name, (String[]) value);
            } else if (value instanceof String) {
                paraMap.put(name, new String[]{(String) value});
            } else {
                paraMap.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

    public String getParameter(String name) {
        String[] values = paraMap.get(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }

    public Map<String, String[]> getParameterMap() {
        return paraMap;
    }

    public String[] getParameterValues(String name) {
        return paraMap.get(name);
    }

}
