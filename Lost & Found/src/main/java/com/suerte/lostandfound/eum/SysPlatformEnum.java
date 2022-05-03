package com.suerte.lostandfound.eum;

import lombok.Getter;

import java.util.EnumSet;

/**
 * @Author: gaoyongfei
 * @Date:  2021/12/10 23:15
 */
@Getter
public enum SysPlatformEnum {

    WINDOWS ("windows"),

    UNIX ("unix");

    private String code;

    SysPlatformEnum(String code) {
        this.code = code;
    }

    public static SysPlatformEnum findByCode (String code) {
        return EnumSet.allOf(SysPlatformEnum.class).stream().filter(e -> e.getCode().equals(code)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported SysPlatformEnum %s", code)));
    }
    
}
