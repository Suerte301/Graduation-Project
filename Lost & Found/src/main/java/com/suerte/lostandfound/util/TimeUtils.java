package com.suerte.lostandfound.util;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @Author: Demon
 * @Date: 2022/5/1
 * @Description:
 */
public class TimeUtils {
    public static String formatDate(Date date) {
        return DateUtil.formatDate(date);
    }

    public static String formatDateTime(Date date) {
        return DateUtil.formatDateTime(date);
    }
}
