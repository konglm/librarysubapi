package com.school.library.bookstorageitembarcode;

/**
 * 具体书籍状态
 */
public enum ItemBarCodeStatusEnum {
    //未确认
    UNCONFIRM(0, "未确认"),
    //已确认
    CONFIRM(5, "已确认");

    private int status;

    private String label;

    ItemBarCodeStatusEnum(int status, String label){
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
