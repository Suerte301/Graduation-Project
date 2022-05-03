package com.suerte.lostandfound.config.minio;

import io.minio.messages.Item;
import io.minio.messages.Owner;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * 存储对象
 * @Author: gaoyongfei
 * @Date:  2021/12/10 21:37
 */
@Data
public class MinioItem {

    // 对象名称
    private String objectName;

    // 最后修改日期
    private Date lastModified;

    private String etag;

    // 对象大小
    private Long size;

    // 存储类
    private String storageClass;

    // 对象执行者
    private Owner owner;

    // 类型
    private String type;

    public MinioItem(String objectName, Date lastModified, String etag, long size, String storageClass, Owner owner, String type) {
        this.objectName = objectName;
        this.lastModified = lastModified;
        this.etag = etag;
        this.size = size;
        this.storageClass = storageClass;
        this.owner = owner;
        this.type = type;
    }


    public MinioItem(Item item) {
        this.objectName = item.objectName();
        if(!item.isDir() && !ObjectUtils.isEmpty(item.lastModified())){
            this.lastModified = Date.from(item.lastModified().toInstant());
        }
        this.etag = item.etag();
        this.size = item.size();
        this.storageClass = item.storageClass();
        this.owner = item.owner();
        this.type = item.isDir() ? "directory" : "file";
    }

}