package com.school.library.bookstorage;

/**
 * 入库状态枚举值
 */
public enum StorageStatusEnum {

    //入库中
    GOING(0),
    //已入库
    END(1);

    private int status;

    StorageStatusEnum(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }

}
