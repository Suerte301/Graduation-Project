package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Data
@TableName("tb_complaint_form")
@Accessors(chain = true)
public class ComplaintForm implements Serializable {
    private static final long serialVersionUID = 5906851957993831411L;
    @TableId(type = IdType.AUTO)
    private String id;
    private Integer uid;
    private String applyId;
    private Integer status;
}
