package com.school.library.userinfo;

/**
 * 用户类型
 */
public enum UserTypeEnum {
    STUDENT("stu"),
    TEACHER("teacher");

    UserTypeEnum(String userType){
        this.userType = userType;
    }

    private String userType;

    public String getUserType() {
        return userType;
    }
}
