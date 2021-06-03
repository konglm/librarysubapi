package com.school.library.borrowbook;

/**
 * 归还状态
 */
public enum ReturnStatusEnum {
    UN_RETURN(0, "未归还"),
    RETURN(1, "已归还"),
    BROKEN(2, "破损"),
    DAMAGE(3, "损毁"),
    LOSE(4, "丢失");

    ReturnStatusEnum(int status, String label){
        this.status = status;
        this.label = label;
    }

    private int status;

    private String label;

    public int getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }
}
