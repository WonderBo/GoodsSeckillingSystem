package com.cqu.wb.redis;

/**
 * Created by jingquan on 2018/7/26.
 */

/**
 * @description Redis中Key的前缀，便于区分不同模块的Key（接口-》抽象类-》实体类：接口起到规范作用）
 */
public interface KeyPrefix {

    public int getExpireSeconds();

    public String getKeyPrefix();
}
