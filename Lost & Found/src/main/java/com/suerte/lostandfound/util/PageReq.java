package com.suerte.lostandfound.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * PageReq
 * @Author: gaoyongfei
 * @Date:  2021/12/3 11:08
 */
@Data
@ApiModel(value = "请求分页基本信息")
public class PageReq {

    @ApiModelProperty(value = "当前页码", required = true)
    @Min(value = 1)
    @NotNull(message = "currPage 不能为null" )
    private Long currPage = 1L;

    @ApiModelProperty(value = "每页显示录数", required = true)
    @Min(value = 10)
    @NotNull(message = "pageSize 不能为null" )
    private Long pageSize = 5L;

    @ApiModelProperty(value = "排序集合" )
    private List<OrderByInfo> orderByList;

}
