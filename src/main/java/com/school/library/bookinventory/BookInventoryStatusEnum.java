package com.school.library.bookinventory;

/**
 * 图书盘点状态
 */
public enum BookInventoryStatusEnum {
    //盘点中
    GOING(0, "盘点中"),
    //盘点结束
    END(1, "已盘点");

    BookInventoryStatusEnum(int status, String label){
        this.status = status;
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
