package com.suerte.lostandfound.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

@Configuration
public class RedissonConfig {
    @Bean
    @ConfigurationProperties(prefix = "redisson")
    public RedissonProperties redissonProperties(){
        return new RedissonProperties();
    }

    @Bean
    public RedissonClient redissonSingle(@Autowired RedissonProperties redissonProperties){
        ResponseEntity.ok();
        Config config=new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setDatabase(redissonProperties.getDatabase());

        if (StringUtils.isNotBlank(redissonProperties.getPassword())){
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

}
