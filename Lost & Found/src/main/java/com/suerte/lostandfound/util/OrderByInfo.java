package com.suerte.lostandfound.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "数据库字段排序信息类")
@Data
public class OrderByInfo {

    // 排序字段
    @ApiModelProperty("排序字段")
    private String field;

    // 排序方式
    @ApiModelProperty("排序方式")
    private String order;

}
