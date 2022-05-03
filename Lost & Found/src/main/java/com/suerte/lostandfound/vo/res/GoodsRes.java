package com.suerte.lostandfound.vo.res;

import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.entity.User;
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
public class GoodsRes {

    private String id;

    // 标题
    private String title;
    // 拾取地址
    private Location location;
    // 详细描述
    private String description;

    // 物品图片地址
    private String imgSrc="emptyImg.png";

    // 物品图片地址
    private List<String> imgSrcList;

    // 用户的uid
    private User user;

    // 发布日期
    private String createDate;
    // 物品状态 :  -1 审核失败 0审核中  1 审核通过  2 结束  3 申诉中 4 申诉失败 5 申诉成功
    private GoodsStatusEnum status;
    // 操作类型:  失物招领/寻物启事
    private OperationEnum operation;
    // 种类
    private Category category;

}
