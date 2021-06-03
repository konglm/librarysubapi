package com.jfnice.handler;

import com.jfinal.upload.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MultipartRequestExtend extends MultipartRequest {

    private Map<String, String[]> paraMap = new HashMap<String, String[]>();

    public MultipartRequestExtend(HttpServletRequest request) {
        super(request);
        paraMap.putAll(super.getParameterMap());
    }

    public MultipartRequestExtend(HttpServletRequest request, String uploadPath) {
        super(request, uploadPath);
        paraMap.putAll(super.getParameterMap());
    }

    public MultipartRequestExtend(HttpServletRequest request, String uploadPath, int maxPostSize) {
        super(request, uploadPath, maxPostSize);
        paraMap.putAll(super.getParameterMap());
    }

    public MultipartRequestExtend(HttpServletRequest request, String uploadPath, int maxPostSize, String encoding) {
        super(request, uploadPath, maxPostSize, encoding);
        paraMap.putAll(super.getParameterMap());
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
