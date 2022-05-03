package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/2/5
 * @Description:
 */
@Data
@TableName("tb_goods")
public class Goods implements Serializable {
    private static final long serialVersionUID = -7616513814342894460L;

    @TableId(type=IdType.AUTO)
    private String id;

    // 标题
    private String title;
    // 拾取地址
    private String locationId;
    // 详细描述
    private String description;

    // 物品图片地址
    private String imgSrc="emptyImg.png";

    // 物品图片地址
    @TableField(exist = false)
    private List<String> imgSrcList=new ArrayList<>();

    // 用户的uid
    private Integer uid;

    // 发布日期
    private Date createDate;
    // 物品状态 :  -1 审核失败 0审核中  1 审核通过  2 结束  3 申诉中 4 申诉失败 5 申诉成功
    private Integer status;
    // 操作类型:  失物招领/寻物启事
    private Integer type;
    // 种类
    private Integer categoryType;


}
