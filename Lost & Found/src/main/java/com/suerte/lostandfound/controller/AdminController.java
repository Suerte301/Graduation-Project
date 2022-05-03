package com.suerte.lostandfound.controller;

import com.suerte.lostandfound.vo.HttpResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Demon
 * @Date: 2022/4/17
 * @Description:
 */
@Controller
@RequestMapping("/admin")
public class AdminController {


    @GetMapping("goodList")
    public HttpResult goodList(){
        // TODO: 2022/4/16 显示所有物品申请信息
        return HttpResult.ok();
    }

    @GetMapping("goodApplication")
    public HttpResult goodApplication(){
        // TODO: 2022/4/16 物品申请信息审核
        return HttpResult.ok();
    }


    @GetMapping("list")
    public HttpResult list(){
        // TODO: 2022/4/16 显示所有申诉信息
        return HttpResult.ok();
    }

    @DeleteMapping("del")
    public HttpResult del(){
        // TODO: 2022/4/16 删除申诉信息
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add(){
        // TODO: 2022/4/16 添加申诉信息
        return HttpResult.ok();
    }

    @PostMapping("update")
    public HttpResult update(){
        // TODO: 2022/4/16 更新申诉信息
        return HttpResult.ok();

    }


}
