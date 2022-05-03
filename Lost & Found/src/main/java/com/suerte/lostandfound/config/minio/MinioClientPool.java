package com.suerte.lostandfound.config.minio;

import io.minio.MinioClient;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * minio 客户端连接池
 * @Author: gaoyongfei
 * @Date:  2021/11/12 15:44
 */
@Data
public class MinioClientPool {

    private GenericObjectPool<MinioClient> minioClientPool;

    public MinioClientPool (MinioProperties minioProperties) {
        MinioClientPoolFactory factory = new MinioClientPoolFactory(minioProperties.getUrl(), minioProperties.getRegion(), minioProperties.getAccessKey(), minioProperties.getSecretKey());
        GenericObjectPoolConfig<MinioClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(minioProperties.getPool().getMaxTotal());
        config.setMaxIdle(minioProperties.getPool().getMaxIdle());
        config.setMinIdle(minioProperties.getPool().getMinIdle());
        config.setMaxWaitMillis(minioProperties.getPool().getMaxWaitMillis());
        config.setBlockWhenExhausted(minioProperties.getPool().isBlockWhenExhausted());
        minioClientPool = new GenericObjectPool<>(factory, config);
    }

}
