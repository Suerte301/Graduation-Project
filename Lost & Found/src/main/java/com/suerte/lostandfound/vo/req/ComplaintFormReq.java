package com.suerte.lostandfound.vo.req;

import com.suerte.lostandfound.util.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Demon
 * @Date: 2022/5/3
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "申请表搜索Req")
public class ComplaintFormReq extends PageReq {
    private Integer status;
}
