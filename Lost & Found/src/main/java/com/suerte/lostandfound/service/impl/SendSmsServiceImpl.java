package com.suerte.lostandfound.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.suerte.lostandfound.constant.RedisConstant;
import com.suerte.lostandfound.service.SendSmsService;
import com.suerte.lostandfound.util.NumberUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Service
public class SendSmsServiceImpl implements SendSmsService {

    @Autowired
    private Client client;

    @Override
    public SendSmsResponse sendSms(String phone,String randomCode) throws Exception {

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("勇往直前")
                .setTemplateCode("SMS_174024755")
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\""+ randomCode +"\"}");

        // 复制代码运行请自行打印 API 的返回值
        return client.sendSms(sendSmsRequest);
    }

}
