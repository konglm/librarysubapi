package com.jfnice.ext;

import com.jfnice.enums.ResultEnum;

/**
 * 一般用于Service进行数据回滚并返回前台的错误信息
 */
@SuppressWarnings("serial")
public class ErrorMsg extends RuntimeException {

    private String code;
    private Object data;

    public ErrorMsg(Throwable cause) {
        super(cause);
    }

    public ErrorMsg(ResultEnum e) {
        super(e.getMsg());
        this.setCode(e.getCode());
    }

    public ErrorMsg(String msg) {
        super(msg);
    }

    public ErrorMsg(String msg, String code) {
        super(msg);
        this.setCode(code);
    }

    public ErrorMsg(String msg, String code, Object data) {
        super(msg);
        this.setCode(code);
        this.setData(data);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
