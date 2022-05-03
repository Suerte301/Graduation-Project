package com.suerte.lostandfound.vo.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/1
 * @Description:
 */
@Data
public class ApplyReq implements Serializable {
    private static final long serialVersionUID = 3265838261032281369L;
    String uid;
    String locationId;
    String locationDetail;
    String tel;
    String goodsId;
}
