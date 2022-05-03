package com.suerte.lostandfound;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@MapperScan(basePackages = "com.suerte.lostandfound.mapper")
@EnableWebSecurity
@EnableAsync
@EnableScheduling
public class LostandfoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostandfoundApplication.class, args);
    }

}
