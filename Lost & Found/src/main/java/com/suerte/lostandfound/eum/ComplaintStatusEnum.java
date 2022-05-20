package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Getter
public enum  ComplaintStatusEnum {
    IN_COMPLAINT("申诉中", 0),
    COMPLAINT_FAILED("申诉失败", -1),
    COMPLAINT_SUCCESS("申诉成功", 1);
//    END("结束", 2);


    private String msg;
    private Integer type;
    ComplaintStatusEnum(String msg, Integer type) {
        this.msg = msg;
        this.type = type;
    }

    public static ComplaintStatusEnum getStatusByType(Integer type) {
        if (type == null) return null;
        for (ComplaintStatusEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
