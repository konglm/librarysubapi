package com.jfnice.enums;

import lombok.Getter;

@Getter
public enum AreaTypeEnum {

    PROVINCE(0, "所有省份"),
    CITY(1, "城市"),
    DISTRICT(2, "区县"),
    ALL_CITY(3, "所有城市"),
    ONE_CITY(4, "获取某个城市信息"),
    ALL_AREA(5, "所有区域信息");

    private int k;
    private String v;

    AreaTypeEnum(int k, String v) {
        this.k = k;
        this.v = v;
    }

}
