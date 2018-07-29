package com.cqu.wb.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by jingquan on 2018/7/29.
 */
public class MD5Util {
    // 表单固定盐值
    private static final String formSalt = "1a2b3c4d";

    /**
     *
     * @param str
     * @return
     * @description md5单向散列加密
     */
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    /**
     *
     * @param inputPass
     * @return
     * @description 第一次md5加密：根据表单固定盐值对用户输入密码进行md5单向散列加密得到用户表单密码，从而使密码在网络中安全传输
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + formSalt.charAt(0) + formSalt.charAt(2) + inputPass + formSalt.charAt(5) + formSalt.charAt(4);
        return md5(str);
    }

    /**
     *
     * @param formPass
     * @param dbSalt 数据库随机盐值
     * @return
     * @description 第二次md5加密：根据数据库随机盐值对用户表单密码进行md5单向散列加密得到用户数据库密码，从而使密码存储安全，避免根据彩虹表破解
     */
    public static String formPassToDBPass(String formPass, String dbSalt) {
        String str = "" + dbSalt.charAt(0) + dbSalt.charAt(2) + formPass + dbSalt.charAt(5) + dbSalt.charAt(4);
        return md5(str);
    }

    /**
     *
     * @param inputPass
     * @param dbSalt
     * @return
     * @description 两次md5加密：对用户输入密码进行两次md5加密得到用户数据库密码
     */
    public static String inputPassToDBPass(String inputPass, String dbSalt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, dbSalt);
        return dbPass;
    }

    public static void main(String[] args) {
        // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(inputPassToFormPass("123456"));
        // b7797cce01b4b131b433b6acf4add449
		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
		System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
    }
}