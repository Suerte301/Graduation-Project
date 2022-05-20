package com.suerte.lostandfound.vo.res;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/5
 * @Description:
 */
@Data
@ApiModel("用户发布物品统计")
public class UserGoodsRes implements Serializable {
    private static final long serialVersionUID = 3879568056234573904L;
    private Integer reviewing;
    private Integer reviewedPass;
    private Integer reviewedFail;
}
