package com.suerte.lostandfound.eum;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/2/5
 * @Description:
 */
@Getter
public enum OperationEnum {
    LOST("失物招领",1),
    FOUND("寻物启事",2),
    ALL("所有",-1);

    OperationEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static OperationEnum getOperationByType(Integer type){
        OperationEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            OperationEnum value = values[i];
            if (value.type.equals(type)){
                return value;
            }
        }
        return ALL;
    }

    public static List<OperationEnum> getDefaultOps(){
        return Arrays.asList(LOST,FOUND);
    }

    private String name;
    private Integer type;
}
