package com.suerte.lostandfound.controller;

import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.exception.CustomException;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.util.PassUtils;
import com.suerte.lostandfound.vo.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String SignUp(User user, HttpServletRequest request) {

        System.out.println(user);

        String password = PassUtils.bCryptEncode(user.getPassword());
        user.setPassword(password);
        user.setUncrypted(user.getPassword());



//        userService.saveOrUpdate()
        HttpResult httpResult = HttpResult.ok(false, "注册失败");
        try {
            httpResult = userService.signUp(user);
        } catch (CustomException e) {
            httpResult = HttpResult.ok(false, e.getMessage());
        }

        request.setAttribute("info", httpResult);

        if (httpResult.getData().equals(true)) {

            return "redirect:/toSignIn";
        }else {
            return "forward:/toSignUp";
        }


    }
}
