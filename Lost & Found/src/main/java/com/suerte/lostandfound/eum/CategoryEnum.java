package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Getter
public enum CategoryEnum {
//    BOOKS("书本",0,"fa-book icon-blue"),
//    VEHICLES("交通工具",1,"fa-motorcycle icon-green"),
//    BEDCLOTHES("床上用品",2,"fa-bed icon-brown"),
//    CREDENTIALS("证件",3,"fa-male icon-violet"),
//    DIGITS("数码电器",4,"fa-mobile-phone icon-dark-blue"),
//    SPORTS("运动",5,"fa-soccer-ball-o icon-orange"),
//    TOOLS("日常工具",6,"fa-gears icon-light-blue"),
//    OTHERS("其他物品",7,"fa-wrench icon-light-green");

    DEFAULT("其他",1,"fa-wrench icon-light-green");

    CategoryEnum(String name, Integer type, String clzName) {
        this.name = name;
        this.type = type;
        this.clzName = clzName;
    }

    private String name;
    private Integer type;
    private String clzName;
}
