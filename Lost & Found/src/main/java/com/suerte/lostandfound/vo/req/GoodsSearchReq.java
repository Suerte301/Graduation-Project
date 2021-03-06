package com.suerte.lostandfound.vo.req;

import com.suerte.lostandfound.util.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @Author: Demon
 * @Date: 2022/4/23
 * @Description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "物品搜索Req")
public class GoodsSearchReq extends PageReq {

    @ApiModelProperty(value = "位置id", required = false)
    @NotNull(message = "positionId 不能为null" )
    private String positionId="-1";
    @ApiModelProperty(value = "类型type", required = false)
    @NotNull(message = "operation 不能为null" )
    private Integer operation=-1;


    @ApiModelProperty(value = "分类type", required = false)
    @NotNull(message = "categoryType 不能为null" )
    private Integer categoryType=-1;

    @ApiModelProperty(value = "搜索的物品title的key", required = false)
    @NotNull(message = "searchKey 不能为null" )
    private String searchKey="";

    @ApiModelProperty(value = "搜索的物品title的key", required = false)
    @NotNull(message = "searchKey 不能为null" )
    private Integer goodStatus=-2;



}
