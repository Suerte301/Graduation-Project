package com.suerte.lostandfound.eum;

import lombok.Getter;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@Getter
public enum FormStatusEnum {
    APPLY_FAILED("申请失败",-1),
    IN_APPLY("申请中",0),
    APPLY_SUCCESS("申请成功",1),
    END("结束", 2),
    IN_COMPLAINT("申诉中", 3),
    COMPLAINT_FAILED("申诉失败", 4),
    COMPLAINT_SUCCESS("申诉成功", 5);

    private String msg;
    private Integer type;
    FormStatusEnum(String msg, Integer type) {
        this.msg = msg;
        this.type = type;
    }

    public static FormStatusEnum getStatusByType(Integer type) {
        if (type == null) return null;
        for (FormStatusEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
