package com.jfnice.enums;

import lombok.Getter;

@Getter
public enum ShakeHandEnum {

    REGISTER("reg", "注册"),
    LOGIN("login", "登陆"),
    RESET_PASSWORD("repw", "重置密码");

    private String k;
    private String v;

    ShakeHandEnum(String k, String v) {
        this.k = k;
        this.v = v;
    }
}
