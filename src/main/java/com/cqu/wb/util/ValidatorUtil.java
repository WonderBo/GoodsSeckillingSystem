package com.cqu.wb.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jingquan on 2018/7/29.
 */
public class ValidatorUtil {
    // 手机号码正则表达式
    private static final String MOBILE_REGEX = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\\d{8}$";

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

        Pattern mobilePattern = Pattern.compile(MOBILE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher mobileMatcher = mobilePattern.matcher(str);

        return mobileMatcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("1535110038"));
    }
}
