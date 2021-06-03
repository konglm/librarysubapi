package com.jfnice.enums;

import lombok.Getter;

@Getter
public enum FinishEnum {

    NO(0, "未毕业"),
    YES(1, "已毕业"),
    ALL(-1, "全部");

    private int k;
    private String v;

    FinishEnum(int k, String v) {
        this.k = k;
        this.v = v;
    }

}