package com.suerte.lostandfound.vo.req;

import com.suerte.lostandfound.util.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "物品Req")
public class GoodsStatusReq  extends PageReq {
    private Integer status;
    private String title;
}
