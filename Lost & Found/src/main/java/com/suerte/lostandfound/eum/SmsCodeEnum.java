package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Getter
public enum SmsCodeEnum {

    OK("OK");

    SmsCodeEnum(String code) {
        this.code = code;
    }

    private String code;
}
