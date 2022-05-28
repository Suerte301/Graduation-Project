package com.suerte.lostandfound.vo.res;

import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.OperationEnum;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import lombok.Data;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/26
 * @Description:
 */
@Data
public class ApplyFormRes {

    private String id;

    private GoodsRes goodsRes;


    private User applyUser;
    private FormStatusEnum status;

    private String popUpDetail;
}
