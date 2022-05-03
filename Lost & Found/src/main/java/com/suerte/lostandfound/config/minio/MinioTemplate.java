package com.suerte.lostandfound.config.minio;

import cn.hutool.core.date.DateUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Minio 模板
 *
 * @Author: gaoyongfei
 * @Date: 2021/12/10 21:53
 */
public class MinioTemplate {

    private final MinioClientPool pool;

    public MinioTemplate(MinioClientPool pool) {
        this.pool = pool;
    }

    /**
     * 创建 bucket
     */
    public void createBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取所有桶
     */
    public List<Bucket> getAllBuckets() throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.listBuckets();
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }



    /**
     * 获取桶对象
     */
    public Optional<Bucket> getBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 删除桶
     */
    public void removeBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 存放对象
     *
     * @param stream      流
     * @param size        文件大小
     * @param contentType 文件类型
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contentType) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1).contentType(contentType).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取桶中所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     文件名前缀
     * @param recursive  是否递归
     */
    public List<MinioItem> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            List<MinioItem> objectList = new ArrayList<MinioItem>();
            Iterable<Result<Item>> objectsIterator = client.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
            objectsIterator.forEach(i -> {
                try {
                    objectList.add(new MinioItem(i.get()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return objectList;
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 删除桶中所有对象
     *
     * @param bucketName 桶名称
     * @param prefix     文件名前缀
     * @param recursive  是否递归
     */
    public boolean removeAllObjectsByPrefix(String bucketName, String prefix, boolean recursive, Date date) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            Iterable<Result<Item>> objectsIterator = client.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
            for (Result<Item> itemResult : objectsIterator) {

                Item item = itemResult.get();
                String s = item.objectName();
                String replace = s.replace(prefix, "").substring(1);
                String currentDate = replace.split("/")[0];

                if (DateUtil.compare(DateUtil.parse(currentDate,"yyyy-MM-dd"),date)<=0){
                    client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                }

            }
            return true;
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取中指定目录下的所有文件的路径
     *
     * @param bucketName
     * @param prefix     文件前缀-就是目录
     */
    public List<String> fileScan(String bucketName, String prefix) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            List<String> fileNamesList = new ArrayList<String>();
            Iterable<Result<Item>> objectsIterator = client.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build());
            objectsIterator.forEach(i -> {
                try {
                    Item item = i.get();
                    String objectName = item.objectName();
                    // 如果是一个目录：就递归处理
                    if (item.isDir()) {
                        fileScan(bucketName, objectName);
                    } else {
                        fileNamesList.add(objectName);
                    }
                    item = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            objectsIterator = null;
            return fileNamesList;
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }


     /**
      * 获取外链接: 永久有效
      */
     public String objectUrl(String bucketName, String objectName) throws Exception {
         MinioClient client = pool.getMinioClientPool().borrowObject();
         try {
             return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                     .bucket(bucketName)
                     .object(objectName)
                     .method(Method.GET)
                     .build());
         } finally {
             if (null != client) {
                 pool.getMinioClientPool().returnObject(client);
             }
         }
     }

     /**
      * 获取外链: 并设置有效时长
      * @param expires 有效期
      */
     public String objectUrl(String bucketName, String objectName, Integer expires, TimeUnit unit) throws Exception {
         MinioClient client = pool.getMinioClientPool().borrowObject();
         try {
             return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                     .bucket(bucketName)
                     .object(objectName)
                     .method(Method.GET)
                     .expiry(expires, unit) // 时间单位
                     .build());
         } finally {
             if (null != client) {
                 pool.getMinioClientPool().returnObject(client);
             }
         }
     }

    /**
     * 上传文件
     */
    public void putFile(String bucketName, String fileName, InputStream stream) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(stream, stream.available(), -1).contentType("application/octet-stream").build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 上传文件,并设置文件类型
     */
    public void putFile(String bucketName, String fileName, InputStream stream, String contentType) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(stream, stream.available(), -1).contentType(contentType).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取文件流
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取文件
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 移除文件
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            // client.listObjects(ListObjectsArgs.builder().prefix(""))
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 移除指定目录下的所有对象
     */
    public void removeObjects(String bucketName, String prefix, boolean recursive) throws Exception {
        List<MinioItem> inList = getAllObjectsByPrefix(bucketName, prefix, recursive);
        for (MinioItem item : inList) {
            removeObject(bucketName, item.getObjectName());
        }
    }


    /**
     * 移除指定目录下的所有对象
     */
    public void removeObjectsByDays(String bucketName, String prefix, boolean recursive, int days, long currentTime) throws Exception {
        List<MinioItem> inList = getAllObjectsByPrefix(bucketName, prefix, recursive);
        for (MinioItem item : inList) {
            if ((currentTime - item.getLastModified().getTime()) >= days * 24 * 60 * 60 * 1000L) {
                removeObject(bucketName, item.getObjectName());
            }
        }
    }

    /**
     * 移除指定目录下的所有对象
     */
    public void removeObjectsByDays(String bucketName, String prefix, boolean recursive, long mills, long currentTime) throws Exception {
        List<MinioItem> inList = getAllObjectsByPrefix(bucketName, prefix, recursive);
        for (MinioItem item : inList) {
            if ((currentTime - item.getLastModified().getTime()) >= mills) {
                removeObject(bucketName, item.getObjectName());
            }
        }
    }

    /**
     * 下载文件
     */
    public void downloadObject(String bucketName, String objectName, String fileName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

}
