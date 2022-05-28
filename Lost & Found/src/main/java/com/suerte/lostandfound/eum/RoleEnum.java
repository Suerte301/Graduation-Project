package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/5/21
 * @Description:
 */
@Getter
public enum RoleEnum {

    USER("用户",2),
    ADMIN("管理员",1);

    RoleEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    private String name;
    private Integer type;

}
