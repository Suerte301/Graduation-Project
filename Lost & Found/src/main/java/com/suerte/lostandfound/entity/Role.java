package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role")
public class Role implements Serializable {
    private static final long serialVersionUID = -8249982696947355208L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("nameZh")
    private String nameZh;
}