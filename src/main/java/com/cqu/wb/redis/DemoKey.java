package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/7/26.
 */

/**
 * @description Demo模块实现Redis中Key的前缀（接口-》抽象类-》实体类：实体类起到根据模块实现具体功能作用）
 */
public class DemoKey extends BasePrefix {

    public DemoKey(String keyPrefix) {
        super(keyPrefix);
    }
    public DemoKey(int expireSeconds, String keyPrefix) {
        super(expireSeconds, keyPrefix);
    }

    public static DemoKey idDemoKey = new DemoKey(30, "id");
    public static DemoKey nameDemoKey = new DemoKey(30, "name");
}
