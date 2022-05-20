package com.suerte.lostandfound.service;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
public interface SendSmsService {
    SendSmsResponse sendSms(String phone,String randomCode) throws Exception;
}
