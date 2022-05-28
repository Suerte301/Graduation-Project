package com.suerte.lostandfound.vo.req;

import com.suerte.lostandfound.util.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Demon
 * @Date: 2022/5/21
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchReq extends PageReq {
    private String searchKey;
    private Integer type;
}
