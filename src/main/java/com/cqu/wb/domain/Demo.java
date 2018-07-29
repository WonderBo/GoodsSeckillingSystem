package com.cqu.wb.domain;

/**
 * Created by jingquan on 2018/7/24.
 */

// PO是持久对象,是O/R映射的时候出现的概，通常对应数据模型(数据库)（数据库与程序对交互介质）

public class Demo {
    private int id;
    private String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
