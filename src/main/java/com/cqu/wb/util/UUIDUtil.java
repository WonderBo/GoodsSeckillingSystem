package com.cqu.wb.util;

import java.util.UUID;

/**
 * Created by jingquan on 2018/7/30.
 */

/**
 * @description UUID：通用唯一识别码,UUID是让分布式系统中的所有元素都能有唯一的辨识信息，而不要要通过中央控制端来做辨识信息的指定。
 */
public class UUIDUtil {

    /**
     *
     * @return
     * @description 生成并获取UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
