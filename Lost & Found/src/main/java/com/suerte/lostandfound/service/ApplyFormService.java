package com.suerte.lostandfound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.vo.res.ApplyRes;
import com.suerte.lostandfound.vo.res.UserApplyRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
public interface ApplyFormService extends IService<ApplyForm> {
     List<UserApplyRes> getApplyFormByUid(Integer uid);
}
