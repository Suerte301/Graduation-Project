package com.suerte.lostandfound.util;

import cn.hutool.core.util.RandomUtil;

/**
 * @Author: Demon
 * @Date: 2022/4/23
 * @Description:
 */
public class NumberUtils {
    public static int round(float a,float b){
        return Math.round(a/b);
    }


    public static String randomCode(){
        return RandomUtil.randomNumbers(6);
    }
}
