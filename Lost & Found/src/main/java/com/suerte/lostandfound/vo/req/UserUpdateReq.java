package com.suerte.lostandfound.vo.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/28
 * @Description:
 */
@Data
public class UserUpdateReq implements Serializable {

    private static final long serialVersionUID = -192771397144055586L;

    private Integer changeId;
    private String changeEmail;
    private String changeQq;
    private String changeTel;

}
