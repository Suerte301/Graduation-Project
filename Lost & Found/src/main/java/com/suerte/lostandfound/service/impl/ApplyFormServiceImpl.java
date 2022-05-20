package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.mapper.ApplyFormMapper;
import com.suerte.lostandfound.service.ApplyFormService;
import com.suerte.lostandfound.vo.res.UserApplyRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@Service
public class ApplyFormServiceImpl extends ServiceImpl<ApplyFormMapper, ApplyForm> implements ApplyFormService {

    @Autowired
    private ApplyFormMapper applyFormMapper;

    @Override
    public List<UserApplyRes> getApplyFormByUid(Integer uid) {
        return applyFormMapper.getApplyFormByUid(uid,
                FormStatusEnum.APPLY_FAILED.getType(),
                FormStatusEnum.IN_APPLY.getType(),
                FormStatusEnum.APPLY_SUCCESS.getType()
//                FormStatusEnum.END.getType()
        );
    }
}
