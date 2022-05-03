package com.suerte.lostandfound.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * minio 配置属性
 * @Author: gaoyongfei
 * @Date:  2021/12/10 21:34
 */
@Data
//@ConditionalOnProperty(name = "kp.minIoEnabled", havingValue = "true")
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    // url
    private String url;

    // 域
    private String region;

    // 账号
    private String accessKey;

    // 密码
    private String secretKey;

    // 桶
    private String bucketName;

    // 线程池配置
    private PoolProperties pool;

    @Data
    public static class PoolProperties {
        // 最大数
        private int maxTotal;

        // 最大空闲数
        private int maxIdle;

        // 最小空闲数
        private int minIdle;

        // 最大空闲时间
        private long maxWaitMillis;

        // 时候阻塞
        private boolean blockWhenExhausted;
    }

}
