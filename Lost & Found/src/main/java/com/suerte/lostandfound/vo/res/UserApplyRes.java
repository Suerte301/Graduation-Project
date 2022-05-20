package com.suerte.lostandfound.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: Demon
 * @Date: 2022/5/6
 * @Description:
 */
@Data
@ApiModel("用户申请物品统计")
public class UserApplyRes {
    Integer failed;
    Integer inProgress;
    Integer success;
//    Integer end;
}
