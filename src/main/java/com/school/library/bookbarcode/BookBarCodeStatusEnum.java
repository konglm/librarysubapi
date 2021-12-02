package com.school.library.bookbarcode;

/**
 * 具体书籍状态
 */
public enum BookBarCodeStatusEnum {
    //未确认
    UNCONFIRM(0, "未确认"),
    //已入库，馆藏
    STORAGE(1, "馆藏"),
    BROKEN(2, "破损"),
    DAMAGE(3, "损毁"),
    LOSE(4, "丢失"),
    CONFIRM(5, "已确认"),
    WRITEOFF(6, "注销"),;

    private int status;

    private String label;

    BookBarCodeStatusEnum(int status, String label){
        this.status = status;
        this.label = label;
    }

    public int getStatus(){
        return this.status;
    }

    public String getLabel() {
        return label;
    }
}
