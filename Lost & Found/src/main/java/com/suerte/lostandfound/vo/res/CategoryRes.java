package com.suerte.lostandfound.vo.res;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/2/5
 * @Description:
 */
@Data
public class CategoryRes implements Serializable {
    private static final long serialVersionUID = -5355438943365526756L;

    private String id;
    private String name;
    private Integer type;
    private String clzName;
    Integer num;
}
