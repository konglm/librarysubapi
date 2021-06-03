package com.jfnice.enums;

import lombok.Getter;

@Getter
public enum UidStatEnum {

    ALL(-1, "全部"),
    NO_ACCOUNT(0, "无账号"),
    YES_ACCOUNT(1, "有账号");

    private int k;
    private String v;

    UidStatEnum(int k, String msg) {
        this.k = k;
        this.v = msg;
    }
}
