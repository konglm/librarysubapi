package com.jfnice.enums;

/**
 * 操作编码
 */
public enum OpCodeEnum {

    INDEX("index"), // 首页
    ADD("add"), // 添加
    EDIT("edit"), // 编辑
    DELETE("delete") // 删除
    ;

    private String v;

    OpCodeEnum(String v) {
        this.v = v;
    }

    public String getV() {
        return this.v;
    }

}
