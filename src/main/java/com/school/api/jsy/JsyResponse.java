package com.school.api.jsy;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class JsyResponse {

    @JSONField(name = "RspCode")
    private String RspCode;
    @JSONField(name = "RspTxt")
    private String RspTxt;
    @JSONField(name = "RspData")
    private String RspData;

    public boolean isOk() {
        return "0000".equals(RspCode);
    }

}
