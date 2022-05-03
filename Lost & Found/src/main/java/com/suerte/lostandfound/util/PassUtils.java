package com.suerte.lostandfound.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
public class PassUtils {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder(12);

    public static String bCryptEncode(String value){
        return bCryptPasswordEncoder.encode(value);
    }
}
