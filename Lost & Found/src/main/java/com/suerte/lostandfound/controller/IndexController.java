package com.suerte.lostandfound.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.suerte.lostandfound.constant.LoginConstant;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.vo.HttpResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Api("跳转页面")
@Controller
@RequestMapping
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"toSignIn", "/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView signin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("signin");

        Object attribute = request.getSession().getAttribute(LoginConstant.USER);
        if (ObjectUtil.isNotNull(attribute)){
            return new ModelAndView("redirect:/user/index");
        }
//        // 判断Cookies是否存在
//        Cookie[] cookies = request.getCookies();
//       if (ObjectUtil.isNotEmpty(cookies)){
//           for (int i = 0; i < cookies.length; i++) {
//               Cookie tmp = cookies[i];
//               if (tmp.getName().equals(LoginConstant.USER)) {
//                   return new ModelAndView("redirect:/user/index");
//               }
//           }
////
//       }
//        if (ObjectUtil.isNotNull(cookie)){
//            response.addCookie(cookie);
//            return modelAndView1;
//        }

        HttpResult info = (HttpResult) request.getAttribute("info");
        System.out.println(info);
        modelAndView.addObject("info", info);
        return modelAndView;
    }

    @GetMapping("404")
    public String error404() {
        return "404";
    }

    @GetMapping("403")
    public String error403() {
        return "403";
    }

    @RequestMapping(value = "toResetPass/{email}", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView toResetPass(@PathVariable("email") String email) {
        ModelAndView modelAndView = new ModelAndView("resetPass");
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @RequestMapping(value = "resetPass", method = {RequestMethod.GET, RequestMethod.POST})
    public String resetPass(String password, String rePass, String email, HttpServletRequest request) {

        if (ObjectUtils.isNotNull(password, rePass) && password.equals(rePass)) {
            String result = new BCryptPasswordEncoder(12).encode(password);
            boolean update = userService.update(new LambdaUpdateWrapper<User>()
                    .eq(ObjectUtils.isNotEmpty(email), User::getEmail, email)
                    .set(ObjectUtils.isNotEmpty(result), User::getPassword, result)
            );
            return "success";
        } else {

            request.setAttribute("info", HttpResult.error("两次输入的密码不一样请重新输入"));
            return "forward:/toResetPass/" + email;
        }

    }

    @RequestMapping(value = "toForgot", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView forgot(HttpServletRequest request) {
        HttpResult info = (HttpResult) request.getAttribute("info");
        System.out.println(info);
        ModelAndView modelAndView = new ModelAndView("forgot");
        modelAndView.addObject("info", info);
        return modelAndView;
    }

    @RequestMapping(value = {"toSignUp"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView signup(HttpServletRequest request) {
        HttpResult info = (HttpResult) request.getAttribute("info");
        System.out.println(info);
        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("info", info);
        return modelAndView;
    }
}
