package com.school.api.gx;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class GxResponse {

    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "code")
    private String code;
    @JSONField(name = "state")
    private String state;
    @JSONField(name = "data")
    private String data;

    public boolean isOk() {
        return "ok".equals(state);
    }

}
