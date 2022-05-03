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
@TableName("tb_category")
@Accessors(chain = true)
public class Category implements Serializable {

    @TableField(exist = false)
    public final static Category DEFAULT= new Category().setName("所有").setType(-1);

    private static final long serialVersionUID = 7042497814552027949L;

    // 主键
    @TableId(type = IdType.AUTO)
    private String id;

    // 名称
    private String name;
    // 描述
    private String description;
    // 描述type
    private Integer type;
    // 排序
    private Integer sort=1;

//    @TableField("clzName")
    // 对应的图标样式
    private String clzName="";


}
