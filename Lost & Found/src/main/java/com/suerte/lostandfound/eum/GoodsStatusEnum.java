package com.suerte.lostandfound.eum;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/26
 * @Description:
 */
@Getter
public enum GoodsStatusEnum {


    REVIEW_FAILED("审核失败", -1),
    REVIEWING("审核中", 0),
    REVIEW_PASSED("审核通过", 1),
    ALL("所有", -2);

    private String msg;
    private Integer type;
    GoodsStatusEnum(String msg, Integer type) {
        this.msg = msg;
        this.type = type;
    }

    public static GoodsStatusEnum getStatusByType(Integer type) {
        if (type == null) return null;
        for (GoodsStatusEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }


    public static List<GoodsStatusEnum> getDefaultOps(){
        return Arrays.asList(REVIEW_FAILED,
                REVIEWING,
                REVIEW_PASSED);
    }

}
