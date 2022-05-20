package com.suerte.lostandfound.vo.res;

import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.entity.User;
import lombok.Data;

/**
 * @Author: Demon
 * @Date: 2022/5/20
 * @Description:
 */
@Data
public class ComplaintRes {
    private String id;
    private User user;
    private ApplyFormRes applyForm;
    private Integer status;
}
