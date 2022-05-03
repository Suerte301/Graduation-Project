package com.suerte.lostandfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.vo.HttpResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Api("发送邮件")
@Controller
@RequestMapping("mail")
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Value("${server.port}")
    private String port;

    @PostMapping(value = "sendEmail")
    public String sendEMail(@Validated @Email String email, HttpServletRequest request) throws MessagingException {

        //判断email是否有账号
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(email), User::getEmail, email));

        if (ObjectUtils.isEmpty(one)){
            request.setAttribute("info", HttpResult.error("当前邮箱没有账号，请重新注册"));
            return "forward:/toForgot";
        }


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        mimeMessageHelper.setSubject("设置密码");
        mimeMessage.setContent("<p>请点击以下连接设置密码: <a href='http://localhost:"+port+"/toResetPass/"+email+"'>设置密码</a></p>", "text/html;charset=utf-8");
        mimeMessageHelper.setFrom("1551706477@qq.com");
        mimeMessageHelper.setTo(email);
        mailSender.send(mimeMessage);
        return "success";
    }
}
