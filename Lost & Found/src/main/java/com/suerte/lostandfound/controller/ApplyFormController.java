package com.suerte.lostandfound.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.OperationEnum;
import com.suerte.lostandfound.service.ApplyFormService;
import com.suerte.lostandfound.service.GoodsService;
import com.suerte.lostandfound.service.LocationService;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.ApplyFormReq;
import com.suerte.lostandfound.vo.res.ApplyRes;
import com.suerte.lostandfound.vo.res.UserApplyRes;
import com.suerte.lostandfound.vo.res.UserGoodsRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Demon
 * @Date: 2022/5/3
 * @Description:
 */
@RequestMapping("applyform")
@Controller
@Slf4j
public class ApplyFormController {


    @Autowired
    private ApplyFormService applyFormService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @GetMapping("/getByStatus")
    @ResponseBody
    public HttpResult getByStatus(Authentication authentication, ApplyFormReq applyFormReq){
        if (ObjectUtil.isNull(applyFormReq.getStatus())||ObjectUtil.isNull(FormStatusEnum.getStatusByType(applyFormReq.getStatus()))){
            return HttpResult.error("传入的类型错误");
        }

        List<String> goodsList = goodsService.list(new LambdaQueryWrapper<Goods>().like(ObjectUtil.isNotEmpty(applyFormReq.getTitle()), Goods::getTitle, applyFormReq.getTitle())).stream().map(Goods::getId).collect(Collectors.toList());
        User principal = (User) authentication.getPrincipal();
        IPage<ApplyForm> page = applyFormService.page(new QueryPage<ApplyForm>().getPage(applyFormReq), new LambdaQueryWrapper<ApplyForm>()
                .eq(ApplyForm::getStatus, applyFormReq.getStatus())
                .in(ObjectUtil.isNotEmpty(goodsList.size()),ApplyForm::getGoodsId,goodsList)
                .eq(ApplyForm::getUid, principal.getId()));
//        PageInfo<ApplyForm> applyPageInfo = new PageInfo<>(page);
        PageInfo applyPageInfo = new PageInfo<>(page);

        List<ApplyRes> list = page.getRecords().stream().map(i->{
            ApplyRes applyRes = new ApplyRes();
            Goods byId = goodsService.getById(i.getGoodsId());
            OperationEnum operationByType = OperationEnum.getOperationByType(byId.getType());
            applyRes.setGoods(byId);
            applyRes.setOperationName(operationByType.getName());
            applyRes.setOperationType(operationByType.getType());
            applyRes.setLocation(byId.getLocationId().equals("-1")? Location.DEFAULT:locationService.getById(byId.getLocationId()));
            applyRes.setStatus(FormStatusEnum.getStatusByType(i.getStatus()));
//            applyRes.setTel(i.getTel());
            applyRes.setUser(userService.getById(i.getUid()));
            applyRes.setId(i.getId());
            applyRes.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));
            return applyRes;
        }).collect(Collectors.toList());

        applyPageInfo.setList(list);

        HashMap<String, Object> map = new HashMap<>();
        map.put("result",applyPageInfo);
        map.put("searchKey", Optional.ofNullable(applyFormReq.getTitle()).orElse(""));

        return HttpResult.ok(map);
    }




    @GetMapping("getApplyByUid/{id}")
    @ResponseBody
    public HttpResult getApplyByUid(@PathVariable("id")Integer id){
        List<UserApplyRes> goodsByUid = applyFormService.getApplyFormByUid(id);

        Map<String,Integer> map = new HashMap<>();
        List<Map<String,Integer>> collect = goodsByUid.stream().map(i -> {

            map.put("申请失败", i==null?0:i.getFailed());
            map.put("申请中", i==null?0:i.getInProgress());
            map.put("申请成功", i==null?0:i.getSuccess());
//            map.put("结束", i==null?0:i.getEnd());
//            map.put("申诉中", i==null?0:i.getInComplaint());
//            map.put("申诉失败", i==null?0:i.getComplaintFailed());
//            map.put("申诉成功", i==null?0:i.getComplaintSuccess());

//            if (i!=null){
//                map.put("申请失败", i.getFailed());
//                map.put("申请中", i.getInProgress());
//                map.put("申请成功", i.getSuccess());
//                map.put("结束", i.getEnd());
//                map.put("申诉中", i.getInComplaint());
//                map.put("申诉失败", i.getComplaintFailed());
//                map.put("申诉成功", i.getComplaintSuccess());
//            }
            return map;
        }).collect(Collectors.toList());
        return HttpResult.ok(collect);
    }



    @DeleteMapping("/del/{id}")
    @ResponseBody
    public HttpResult del(@PathVariable("id")String id){
        boolean flag=true;
        try {
            flag = applyFormService.removeById(id);
        }catch (Exception e){
            log.error("撤销申请失败 报错原因 {} 报错位置 {}",e.getMessage(), Arrays.toString(e.getStackTrace()));
            flag=false;
        }
        return flag?HttpResult.ok("撤销申请成功"):HttpResult.error("撤销申请失败");
    }
}
