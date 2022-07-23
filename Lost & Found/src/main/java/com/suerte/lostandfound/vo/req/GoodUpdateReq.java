package com.suerte.lostandfound.vo.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.sql.In;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/28
 * @Description:
 */

@Data
public class GoodUpdateReq implements Serializable {
    private static final long serialVersionUID = -6789074682445804146L;
    private String changeId;
    private String changeTitle;
    private String changeLocation;
    private String changeDes;
    private Integer changeUser;
    private Integer changeOperation;
    private String changeCategory;
    private Integer changeStatus;
}
