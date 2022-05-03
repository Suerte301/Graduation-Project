package com.suerte.lostandfound.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.service.GoodsService;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.GoodsStatusReq;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/16
 * @Description:
 */
@Api("奖品")
@RequestMapping("goods")
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("list")
    @ResponseBody
    public HttpResult list(){
        // TODO: 2022/4/16 显示所有物品
        List<Goods> list = goodsService.list();
        return HttpResult.ok(list);
    }

    @GetMapping("/getByStatus")
    @ResponseBody
    public HttpResult getByStatus(Authentication authentication, GoodsStatusReq goodsStatusReq){
        if (ObjectUtil.isNull(goodsStatusReq.getStatus())||ObjectUtil.isNull(GoodsStatusEnum.getStatusByType(goodsStatusReq.getStatus()))){
            return HttpResult.error("传入的类型错误");
        }
         User principal = (User) authentication.getPrincipal();
        IPage<Goods> page = goodsService.page(new QueryPage<Goods>().getPage(goodsStatusReq), new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, goodsStatusReq.getStatus())
                .eq(Goods::getUid, principal.getId()));
        PageInfo<Goods> goodsPageInfo = new PageInfo<>(page);

        List<Goods> list = goodsPageInfo.getList();
        list.forEach(i -> {
            String[] split = i.getImgSrc().split(",");
//            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
//                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
//                split[0]=ImgConstant.DEFAULT_IMG_PATH;
//            }
            i.setImgSrc(split[0]);
            i.setImgSrcList(Arrays.asList(split));
        });
        goodsPageInfo.setList(list);

        return HttpResult.ok(goodsPageInfo);
    }

    @DeleteMapping("del")
    public HttpResult del(){
        // TODO: 2022/4/16 删除物品
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add(){
        // TODO: 2022/4/16 添加物品
        return HttpResult.ok();

    }
    @PostMapping("update")
    public HttpResult update(){
        // TODO: 2022/4/16 更新物品状态
        return HttpResult.ok();

    }


}
