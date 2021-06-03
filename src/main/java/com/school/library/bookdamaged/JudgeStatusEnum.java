package com.school.library.bookdamaged;

/**
 * 押金扣除审核状态
 */
public enum JudgeStatusEnum {
    UNAUDIT( 0,"未审核"),
    AUDIT( 1,"审核通过"),
    AUDITNOTDEDUCTION( 2,"审核通过不扣钱");


    private int status;

    private String label;

    JudgeStatusEnum(int status, String label){
        this.status = status;
        this.label = label;
    }

    public int getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }
}
