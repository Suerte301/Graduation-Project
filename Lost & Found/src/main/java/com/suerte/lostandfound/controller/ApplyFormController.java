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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
            applyRes.setLocation(i.getLocationId().equals("-1")? Location.DEFAULT:locationService.getById(i.getLocationId()));
            applyRes.setLocationDetail(i.getLocationDetail());
            applyRes.setStatus(FormStatusEnum.getStatusByType(i.getStatus()));
            applyRes.setTel(i.getTel());
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
