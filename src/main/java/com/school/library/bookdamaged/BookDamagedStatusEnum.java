package com.school.library.bookdamaged;

public enum BookDamagedStatusEnum {
    //已确认
    CONFIRM(1, "已修复"),
    BROKEN(2, "破损"),
    DAMAGE(3, "损毁"),
    LOSE(4, "丢失");

    private int status;

    private String label;

    BookDamagedStatusEnum(int status, String label){
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
