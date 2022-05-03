package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Data
@TableName("tb_location")
@Accessors(chain = true)

public class Location implements Serializable {

    @TableField(exist = false)
    public final static Location DEFAULT= new Location().setPosition("所有").setId("-1");

    private static final long serialVersionUID = -8215773512531490936L;

    @TableId(type= IdType.AUTO)
    private String id;
    private String position;

}
