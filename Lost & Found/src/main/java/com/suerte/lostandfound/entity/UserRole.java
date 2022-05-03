package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Data
@TableName("sys_user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = -6503896055404465370L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer rid;
}
