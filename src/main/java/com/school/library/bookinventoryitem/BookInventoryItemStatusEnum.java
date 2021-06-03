package com.school.library.bookinventoryitem;

/**
 * 图书盘点明细状态
 */
public enum BookInventoryItemStatusEnum {
    CONFIRM(1, "已确认"),
    UN_CONFIRM(0, "未确认");

    BookInventoryItemStatusEnum(int status, String label){
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
