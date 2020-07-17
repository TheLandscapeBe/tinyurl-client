package com.github.tinyurl.client.util;

/**
 * 数字工具类
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
public class NumberUtil {

    public static boolean isNumber(String s) {
        if (s != null && s.length() > 0) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
