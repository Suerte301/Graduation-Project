package com.suerte.lostandfound.controller;

import com.suerte.lostandfound.entity.Prize;
import com.suerte.lostandfound.vo.HttpResult;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

/**
 * @Author: Demon
 * @Date: 2022/4/16
 * @Description:
 */
@Api("奖品  (笔记本,笔,球,申诉次数等)")
@RequestMapping("prize")
@Controller
public class PrizeController {

    @GetMapping("list")
    public HttpResult list(){
        // TODO: 2022/4/16 显示所有奖品
        ArrayList<Prize> prizes = new ArrayList<>();
        return HttpResult.ok(prizes);
    }

    @GetMapping("hotList")
    public HttpResult hotList(){
        // TODO: 2022/4/16 显示所有热门奖品  ---  使用redis的ttl过期时间来做
        ArrayList<Prize> prizes = new ArrayList<>();
        return HttpResult.ok(prizes);
    }

    @DeleteMapping("del")
    public HttpResult del(){
        // TODO: 2022/4/16 删除奖品
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add(){
        // TODO: 2022/4/16 添加奖品
        return HttpResult.ok();

    }
    @PostMapping("update")
    public HttpResult update(){
        // TODO: 2022/4/16 更新奖品状态
        return HttpResult.ok();

    }

}
