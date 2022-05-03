package com.suerte.lostandfound.vo.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/3
 * @Description:
 */
@Data
public class AddGoodsReq implements Serializable {
    private static final long serialVersionUID = -5103736342030203531L;
    private Integer uid;
    private String title;
    private Integer category;
    private Integer operation;
    private String location;

    private String description;
}
