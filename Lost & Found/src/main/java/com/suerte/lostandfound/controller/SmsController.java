package com.suerte.lostandfound.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.suerte.lostandfound.constant.RedisConstant;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.eum.SmsCodeEnum;
import com.suerte.lostandfound.service.SendSmsService;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@RequestMapping("sms")
@RestController
@Slf4j
public class SmsController {

    @Autowired
    private SendSmsService sendSmsService;

    @Autowired
    private RedissonClient redissonClient;

    @PostMapping("send")
    public String send(@RequestParam("phone")String phone){
        try {
            String randomCode = NumberUtils.randomCode();
            log.info("电话 {} 验证码 {}",phone,randomCode);
            SendSmsResponse sendSmsResponse = sendSmsService.sendSms(phone,randomCode);
             if (sendSmsResponse.getBody().getCode().equals(SmsCodeEnum.OK.getCode())){
                log.info("message {}",sendSmsResponse.getBody().getMessage());
                log.info("code {}",sendSmsResponse.getBody().getCode());
                log.info("bizId {}",sendSmsResponse.getBody().getBizId());
                log.info("requestId {}",sendSmsResponse.getBody().getRequestId());
                log.info("header {}",sendSmsResponse.getHeaders());
                redissonClient.getBucket(RedisConstant.SMS_CODE_PREFIX+phone).set(randomCode,1, TimeUnit.MINUTES);
                return "success";
            }
        } catch (Exception e) {
            log.error("发送信息失败 报错信息 {} 报错位置 {}",e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return "error";
    }

    @GetMapping("getCode")
    public String getCode(@RequestParam("account") String account){
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(ObjectUtil.isNotEmpty(account), User::getAccount, account));
        String o =null;
        if (one!=null){
            final Object tmp = redissonClient.getBucket(RedisConstant.SMS_CODE_PREFIX + one.getTel()).get();
            o= tmp==null?"":tmp+"";
        }
        return o;
    }

    @Autowired
    private UserService userService;

    @GetMapping("getByAccount")
    public String getByAccount(@RequestParam("account") String account){

        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(ObjectUtil.isNotEmpty(account), User::getAccount, account));

        if (one!=null){
            return one.getTel();
        }
        return "";
    }


}
