package com.cqu.wb.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jingquan on 2018/7/29.
 */
public class ValidatorUtil {
    // 手机号码正则表达式
    private static final String MOBILE_REGEX = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\\\d{8}$";

    /**
     *
     * @param str
     * @return
     * @description 验证手机号码是否符合规范
     */
    public static boolean isMobile(String str) {
        // 输入验证
        if(StringUtils.isEmpty(str)) {
            return false;
        }

        Pattern mobilePattern = Pattern.compile(MOBILE_REGEX);
        Matcher mobileMatcher = mobilePattern.matcher(str);

        return mobileMatcher.matches();
    }
}
