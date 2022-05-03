package com.suerte.lostandfound.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomException extends Exception {

    String code;
    String msg;

    public CustomException(String message) {
        super(message);
        this.msg=message;
    }

    public CustomException(String message, String code, String msg) {
        super(message);
        this.code = code;
        this.msg = msg;
    }
}
