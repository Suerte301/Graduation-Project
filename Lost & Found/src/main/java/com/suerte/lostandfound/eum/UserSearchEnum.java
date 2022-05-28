package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/5/21
 * @Description:
 */
@Getter
public enum UserSearchEnum {

    ACCOUNT("账户",0),
    SID("学号",1),
    NAME("名字",2);

    UserSearchEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    private String name;
    private Integer type;


    public static UserSearchEnum getUserSearchTypeByType(Integer type){
        UserSearchEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            UserSearchEnum value = values[i];
            if (value.type.equals(type)){
                return value;
            }
        }
        return null;
    }
}
