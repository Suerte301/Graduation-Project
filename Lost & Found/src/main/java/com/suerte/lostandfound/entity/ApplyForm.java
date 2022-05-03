package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@Data
@TableName("tb_applyform")
@Accessors(chain = true)
public class ApplyForm implements Serializable {
    private static final long serialVersionUID = 3265838261032281369L;

    @TableId(type = IdType.AUTO)
    String id;

    Integer uid;

    String locationId;

    String locationDetail;
    // 用户电话
    String tel;
    // 物品Id
    String goodsId;
}
