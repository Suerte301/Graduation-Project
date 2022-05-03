package com.suerte.lostandfound.config.minio;

import io.minio.StatObjectResponse;
import lombok.Data;

import java.util.Date;

/**
 * minio 存储对象
 * @Author: gaoyongfei
 * @Date:  2021/11/12 15:52
 */
@Data
public class MinioObject {

    // 桶名称
    private String bucketName;

    // 文件名称
    private String name;

    // 最后修改日期
    private Date lastModified;

    // 上传的文件大小
    private long length;

    private String etag;

    // 文件类型
    private String contentType;

    public MinioObject() {
    }

    public MinioObject(String bucketName, String name, Date lastModified, long length, String etag, String contentType) {
        this.bucketName = bucketName;
        this.name = name;
        this.lastModified = lastModified;
        this.length = length;
        this.etag = etag;
        this.contentType = contentType;
    }

    public MinioObject(StatObjectResponse os) {
        this.bucketName = os.bucket();
        this.name = os.object();
        this.lastModified = Date.from(os.lastModified().toInstant());
        this.length = os.size();
        this.etag = os.etag();
        this.contentType = os.contentType();
    }

}