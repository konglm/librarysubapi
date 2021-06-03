package com.jfnice.qiniu;

import lombok.Data;

@Data
public class Resp {

    private String Status;
    private String Message;
    private String Data;

    public Resp(String Status, String Message, String Data) {
        this.Status = Status;
        this.Message = Message;
        this.Data = Data;
    }

    public boolean isOk() {
        return "1".equals(Status);
    }

}
