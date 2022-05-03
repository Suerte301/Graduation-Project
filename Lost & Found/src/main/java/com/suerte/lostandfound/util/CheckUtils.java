package com.suerte.lostandfound.util;

import cn.hutool.core.util.ObjectUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Demon
 * @Date: 2022/5/1
 * @Description:
 */
public class CheckUtils {
    /**
     * 判断是否为正确的手机号
     * @param number
     * @return
     */
    public static boolean isMobileNO(String number) {
        if (ObjectUtil.isEmpty(number)){
            return false;
        }
        if (number.startsWith("+86")) {
            number = number.substring(3);
        }

        if (number.startsWith("+") || number.startsWith("0")) {
            number = number.substring(1);
        }

        number = number.replace(" ", "").replace("-", "");
        Pattern p = Pattern.compile("^((13[0-9])|(14[5-9])|(15[0-3])|(15[5-9])|(166)|(17[0-8])|(18[0-9])|(19[8-9]))\\d{8}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }
}
