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
public class ComplaintUpdateReq implements Serializable {
    private static final long serialVersionUID = -4409471549837754179L;

    private String changeId;
    private String changeLocation;
    private Integer changeOperation;
    private Integer changeCategory;
    private Integer changeStatus;
    private Integer changeComplainUser;
}
