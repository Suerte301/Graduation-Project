package com.suerte.lostandfound.controller;

import com.suerte.lostandfound.vo.HttpResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @Author: Demon
 * @Date: 2022/2/2
 * @Description:
 */
@Controller
@RequestMapping({"test","admin"})
@SessionAttributes({"test","test2"})
public class TestController {

    @GetMapping("clock")
    public String clock(){
        return "test";
    }
    @GetMapping("testHtml")
    @ResponseBody
    public HttpResult testHtml(){
        return HttpResult.ok("test thymeleaf");
    }

    @GetMapping({"index","/"})
    public String index(){
        return "index";
    }

    @GetMapping({"signup"})
    public String signup(){
        return "signup";
    }

    @GetMapping("category")
    public String category(String type){
        return "category";
    }
    @GetMapping("detail")
    public String detail(){
        return "detail";
    }

    @GetMapping("addSession")
    @ResponseBody
    public HttpResult addSession(Model model){
        // 只能用model，不能用 ModelAndView
        model.addAttribute("test","test213");
        model.addAttribute("test1","test465");
        return HttpResult.ok("success");
    }

    @GetMapping("testSession")
    public String testSession(@SessionAttribute("test")Object test){
//        Object test = session.getAttribute("test");
        System.out.println(test);
        return "detail";
    }
    @GetMapping("clearSession")
    @ResponseBody
    public HttpResult clearSession(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return HttpResult.ok("success");
    }





}
