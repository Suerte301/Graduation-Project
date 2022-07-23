package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.entity.ComplaintForm;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.service.*;
import com.suerte.lostandfound.vo.req.ApplyFormUpdateReq;
import com.suerte.lostandfound.vo.req.ComplaintUpdateReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Demon
 * @Date: 2022/5/28
 * @Description:
 */
@Service
@Transactional
@Slf4j
public class AdminServiceImpl implements AdminService {


    @Autowired
    private UserService userService;

    @Autowired
    private ComplaintFormService complaintFormService;

    @Autowired
    private ApplyFormService applyFormService;

    @Autowired
    private GoodsService goodsService;


    @Override
    @Transactional
    public void delUser(Integer id) {
        userService.removeById(id);
        final List<String> goods = goodsService.list(new LambdaQueryWrapper<Goods>().eq(Goods::getUid, id)).stream().map(Goods::getId).collect(Collectors.toList());
        userService.removeByIds(goods);
        final List<String> apply = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getUid, id)).stream().map(ApplyForm::getId).collect(Collectors.toList());
        applyFormService.removeByIds(apply);
        final List<String> complaint = complaintFormService.list(new LambdaQueryWrapper<ComplaintForm>().eq(ComplaintForm::getUid, id)).stream().map(ComplaintForm::getId).collect(Collectors.toList());
        applyFormService.removeByIds(complaint);
    }


    @Override
    @Transactional
    public void delGoods(String id) {

        goodsService.removeById(id);
        final List<String> apply = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getGoodsId, id)).stream().map(ApplyForm::getId).collect(Collectors.toList());
        applyFormService.removeByIds(apply);

        if (!apply.isEmpty()) {
            final List<String> collect = complaintFormService.list(new LambdaQueryWrapper<ComplaintForm>().in(ComplaintForm::getApplyId, apply)).stream().map(ComplaintForm::getId).collect(Collectors.toList());
            applyFormService.removeByIds(collect);
        }
    }


    @Override
    @Transactional
    public void delComplaint(String id) {

        final ComplaintForm complaintForm = complaintFormService.getById(id);

        complaintFormService.removeById(id);

        final ApplyForm applyForm = applyFormService.getById(complaintForm.getApplyId());

        applyForm.setStatus(FormStatusEnum.APPLY_SUCCESS.getType());

        applyFormService.saveOrUpdate(applyForm);

    }


    @Override
    @Transactional
    public void updateComplaintForm(ComplaintUpdateReq complaintUpdateReq) throws Exception {

        ComplaintForm complaintForm = complaintFormService.getById(complaintUpdateReq.getChangeId());
        ApplyForm applyForm = applyFormService.getById(complaintForm.getApplyId());
        Goods goods = goodsService.getById(applyForm.getGoodsId());

        goods.setLocationId(complaintUpdateReq.getChangeLocation());
        goods.setType(complaintUpdateReq.getChangeOperation());
        goods.setCategoryType(complaintUpdateReq.getChangeCategory());

        goodsService.saveOrUpdate(goods);
        complaintForm.setStatus(complaintUpdateReq.getChangeStatus());
        complaintForm.setUid(complaintUpdateReq.getChangeComplainUser());
        complaintFormService.saveOrUpdate(complaintForm);

    }

    @Override
    @Transactional
    public void updateForm(ApplyFormUpdateReq applyFormUpdateReq) throws Exception {

        final ApplyForm applyForm = applyFormService.getById(applyFormUpdateReq.getChangeId());

        final Goods goods = goodsService.getById(applyForm.getGoodsId());

        goods.setType(applyFormUpdateReq.getChangeOperation());
        goods.setCategoryType(applyFormUpdateReq.getChangeCategory());
        goods.setLocationId(applyFormUpdateReq.getChangeLocation());

        if (applyFormUpdateReq.getChangeStatus().equals(FormStatusEnum.APPLY_SUCCESS.getType())){
            goods.setStatus(GoodsStatusEnum.END.getType());
        }else {
            goods.setStatus(GoodsStatusEnum.REVIEW_PASSED.getType());
        }

        applyForm.setUid(applyForm.getUid());
        applyForm.setStatus(applyFormUpdateReq.getChangeStatus());
        goodsService.saveOrUpdate(goods);


        applyFormService.saveOrUpdate(applyForm);
    }
}
