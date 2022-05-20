package com.suerte.lostandfound.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suerte.lostandfound.entity.*;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.eum.OperationEnum;
import com.suerte.lostandfound.service.*;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.ApplyFormReq;
import com.suerte.lostandfound.vo.req.ComplaintFormReq;
import com.suerte.lostandfound.vo.res.ApplyFormRes;
import com.suerte.lostandfound.vo.res.ApplyRes;
import com.suerte.lostandfound.vo.res.ComplaintRes;
import com.suerte.lostandfound.vo.res.GoodsRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.stream.Collectors;

/**
 * @Author: Demon
 * @Date: 2022/5/20
 * @Description:
 */

@RequestMapping("complaint")
@Controller
@Slf4j
public class ComplaintFormController {

    @Autowired
    private ComplaintFormService complaintFormService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ApplyFormService applyFormService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getByStatus")
    @ResponseBody
    public HttpResult getByStatus(Authentication authentication, ComplaintFormReq complaintFormReq){

//        List<String> goodsList = goodsService.list(new LambdaQueryWrapper<Goods>().like(ObjectUtil.isNotEmpty(applyFormReq.getTitle()), Goods::getTitle, applyFormReq.getTitle())).stream().map(Goods::getId).collect(Collectors.toList());
        User principal = (User) authentication.getPrincipal();
        IPage<ComplaintForm> page = complaintFormService.page(new QueryPage<ComplaintForm>().getPage(complaintFormReq), new LambdaQueryWrapper<ComplaintForm>()
                .eq(ComplaintForm::getStatus, complaintFormReq.getStatus())
                .eq(ComplaintForm::getUid, principal.getId()));
//        PageInfo<ApplyForm> applyPageInfo = new PageInfo<>(page);
        PageInfo applyPageInfo = new PageInfo<>(page);

        List<ComplaintRes> list = page.getRecords().stream().map(i->{
            ComplaintRes complaintRes = new ComplaintRes();

            complaintRes.setUser(userService.getById(i.getUid()));
            final ApplyFormRes applyFormRes = new ApplyFormRes();
            final ApplyForm byId = applyFormService.getById(i.getApplyId());
            applyFormRes.setId(byId.getId());

            applyFormRes.setApplyUser(userService.getById(byId.getUid()));

            final Goods goods = goodsService.getById(byId.getGoodsId());


            final GoodsRes goodsRes = new GoodsRes();
//            goodsRes.setOperation(OperationEnum.getOperationByType(i.getType()));
            OperationEnum iOperationByType = OperationEnum.getOperationByType(goods.getType());
            goodsRes.setOperation(iOperationByType);
            goodsRes.setOperationName(iOperationByType.getName());
            goodsRes.setOperationType(iOperationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(goods.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(goods.getUid()));
            goodsRes.setImgSrcList(goods.getImgSrcList());
            goodsRes.setImgSrc(goods.getImgSrc());
            goodsRes.setDescription(goods.getDescription());
            goodsRes.setLocation(goods.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(goods.getLocationId()));
            goodsRes.setTitle(goods.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, goods.getCategoryType())));
            goodsRes.setId(goods.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(goods.getCreateDate()));




            applyFormRes.setGoodsRes(goodsRes);
            String imgSrcStr=ObjectUtil.isNotEmpty(goodsRes.getImgSrc())?goodsRes.getImgSrc():"/icons/emptyImg.png";
            applyFormRes.setPopUpDetail("标题: <span>" + goodsRes.getTitle() + "</span>" +"<br>"+
                    "拾取地址: <span>" + goodsRes.getLocation().getPosition() + "</span>" +"<br>"+
                    "描述: <span>" + goodsRes.getDescription() + "</span>" +"<br>"+
                    "图片: <img src='" + imgSrcStr + "'/>" +"<br>"+
                    "发布用户: <span>" + goodsRes.getUser().getName() + "</span>" +"<br>"+
                    "类型: <span>" + goodsRes.getOperationName() + "</span>" +"<br>"+
                    "发布日期: <span>" + goodsRes.getCreateDate() + "</span>" +"<br>"+
                    "申请人: <span>" + applyFormRes.getApplyUser().getName() + "</span>");


            complaintRes.setApplyForm(applyFormRes);
            complaintRes.setStatus(i.getStatus());
            complaintRes.setId(i.getId());

            return complaintRes;
        }).collect(Collectors.toList());

        applyPageInfo.setList(list);

        HashMap<String, Object> map = new HashMap<>();
        map.put("result",applyPageInfo);

        return HttpResult.ok(map);
    }
}
