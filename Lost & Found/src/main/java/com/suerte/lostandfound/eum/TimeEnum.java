package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/2/5
 * @Description:
 */
@Getter
public enum TimeEnum {
    TODAY("今天",0),
    WEEK("一周内",1),
    MONTH("一个月内",2),
    THREEMONTH("三个月内",3),
    HALFYEAR("半年内",4),
    YEAR("一年内",5),
    ALL("所有",-1);

    TimeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static TimeEnum getOperationByType(Integer type){
        TimeEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            TimeEnum value = values[i];
            if (value.type.equals(type)){
                return value;
            }
        }
        return ALL;
    }

    private String name;
    private Integer type;
}
