package com.suerte.lostandfound.vo.res;

import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.eum.FormStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Demon
 * @Date: 2022/5/1
 * @Description:
 */
@Data
public class ApplyRes implements Serializable {
    private static final long serialVersionUID = 3265838261032281369L;
    private String id;
    private User user;
    private Location location;
    private String locationDetail;
    private String tel;
    private Goods goods;
    private String operationName;
    private Integer operationType;
    private FormStatusEnum status;
    private String createDate;
}
