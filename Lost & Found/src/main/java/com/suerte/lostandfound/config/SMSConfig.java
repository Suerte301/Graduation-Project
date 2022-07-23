package com.suerte.lostandfound.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Configuration
public class SMSConfig {


    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    @SneakyThrows
    @Bean
    public Client client(){
//        return createClient("LTAI5tNXpHLTKWVrJYDCqA5Z", "FiTwJl7xscwThVh9KKgy5k8Ji4dg3X");
        return createClient("", "");
    }

    public Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }
}
