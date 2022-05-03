package com.suerte.lostandfound.config.minio;

import io.minio.MinioClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 连接池工厂
 * @Author: gaoyongfei
 * @Date:  2021/11/12 15:44
 */
public class MinioClientPoolFactory extends BasePooledObjectFactory<MinioClient> {

    // url
    private String url;

    // 域
    private String region;

    // 账号
    private String accessKey;

    // 密码
    private String secretKey;

    public MinioClientPoolFactory (String url, String region, String accessKey, String secretKey) {
        this.url = url;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public MinioClient create() {
        return MinioClient.builder().endpoint(this.url).region(region).credentials(this.accessKey, this.secretKey).build();
    }

    @Override
    public PooledObject<MinioClient> makeObject() throws Exception {
        return super.makeObject();
    }

    @Override
    public PooledObject<MinioClient> wrap(MinioClient minioClient) {
        return new DefaultPooledObject<>(minioClient);
    }

}
