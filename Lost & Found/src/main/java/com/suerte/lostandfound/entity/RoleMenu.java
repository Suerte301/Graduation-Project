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
@TableName("sys_role_menu")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = -1758645310746720867L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer rid;
    private Integer mid;

}
