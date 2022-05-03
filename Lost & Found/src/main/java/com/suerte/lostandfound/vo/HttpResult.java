package com.suerte.lostandfound.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一响应类
 */
@Data
public class HttpResult implements Serializable {
    private static final long serialVersionUID = -7230107349650732108L;
    private int code = 200;
    private String msg;
    private Object data;

    public static HttpResult error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static HttpResult error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static HttpResult error(int code, String msg) {
        HttpResult r = new HttpResult();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
    public static HttpResult error(List<String> errorList){
        HttpResult r = new HttpResult();
        r.setData(errorList);
        r.setCode(1000);
        return r;
    }

    public static HttpResult ok(String msg) {
        HttpResult r = new HttpResult();
        r.setMsg(msg);
        return r;
    }

    public static HttpResult ok(Object data) {
        HttpResult r = new HttpResult();
        r.setData(data);
        return r;
    }
    public static HttpResult ok(Object data,String msg) {
        HttpResult r = new HttpResult();
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
    public static HttpResult ok() {
        return new HttpResult();
    }

}

