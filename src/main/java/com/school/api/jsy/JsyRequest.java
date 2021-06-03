package com.school.api.jsy;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class JsyRequest {

    private String action;
    private Map<String, Object> paraMap;
    private Map<String, String> headers = new HashMap<>();

    public JsyRequest(String action, Map<String, Object> paraMap) {
        headers.put("Content-Type", "application/json; charset=UTF-8");
        this.action = action;
        this.paraMap = paraMap == null ? new HashMap<>() : paraMap;
    }

    public Map<String, Object> paraMap() {
        paraMap.remove("headers");
        paraMap.remove("action");
        paraMap.remove("responseClass");
        return paraMap;
    }

}
