package com.jfnice.enums;

import lombok.Getter;

/**
 * 返回结果编码枚举类
 */
@Getter
public enum ResultEnum {

    SUCCESS("0000", "成功"),
    URL_PARA_ERROR("0001", "Url或数据被篡改"),
    SESSION_TIMEOUT("0002", "会话信息过期或不存在"),
    VERIFY_CODE_ERROR("0003", "验证码不正确"),
    ACCOUNT_NOT_EXIST("0004", "账户不存在"),
    ACCOUNT_PWD_ERROR("0005", "用户名或密码不正确"),
    TOKEN_TIMEOUT("0006", "令牌已过期或不存在"),
    AUTHORITY_ERROR("0007", "操作权限不对应"),
    RECORD_ERROR("0008", "操作记录失败"),
    RECORD_NOT_EXIST("0009", "查询记录为空"),
    SESSION_NOT_EXIST("0010", "验证信息不存在需要重新登录"),
    PARA_INVALID("0011", "传输的数据非法"),
    CMD_ERROR("0012", "传送命令为空"),
    NOT_LOGIN("0013", "用户没有登录,关闭当前页,重新从企业管理端登录"),
    RECORD_DUPLICATE("0014", "记录重复"),
    ERROR_SIGN("0015", "参数签名验证不通过！"),
    SYSTEM_ERROR("9999", "系统异常"),
    ;

    private String code;
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
