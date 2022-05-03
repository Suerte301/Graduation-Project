package com.suerte.lostandfound.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */

/**
 * 错误页面的配置
 */
@Configuration
public class ErrorPagesConfig {
    @Bean   //此注解一定记住要加上，别忘记
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> containerCustomizer(){
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                //状态码               错误页面的存储路径
                ErrorPage errorPage400 = new ErrorPage(HttpStatus.BAD_REQUEST, "/404");
                ErrorPage errorPage403 = new ErrorPage(HttpStatus.FORBIDDEN, "/403");
                ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
                ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/404");
//...可自己一个一个的补全
                factory.addErrorPages(errorPage403,errorPage404);
            }

        };
    }
}