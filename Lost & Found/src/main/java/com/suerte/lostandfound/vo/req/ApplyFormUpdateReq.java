package com.suerte.lostandfound.vo.req;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/28
 * @Description:
 */
@Data
public class ApplyFormUpdateReq implements Serializable {

    private static final long serialVersionUID = -192771397144055586L;

    private String changeId;
    private Integer changeStatus;
    private String changeLocation;
    private Integer changeOperation;
    private Integer changeCategory;
    private Integer changeApplyUser;


}
