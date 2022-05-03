package com.suerte.lostandfound.config.minio;


import com.suerte.lostandfound.eum.SysPlatformEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: gaoyongfei
 * @Date:  2021/11/12 15:45
 */
@Data
public class MinioHelper {

    private String separator;

    private final MinioTemplate template;

    /**
     * content-value 与 文件类型的映射
     */
    private  Map<String, String> contentTypeKeyMap = new HashMap<>();
    private  Map<String, String> contentTypeValueMap = new HashMap<>();

    @PostConstruct
    public void init(){
        contentTypeKeyMap = new HashMap<>();
        contentTypeValueMap = new HashMap<>();

        contentTypeKeyMap.put("shtml","text/html");
        contentTypeKeyMap.put("giff","application/octet-stream");
        contentTypeKeyMap.put("gd","application/octet-stream");
        contentTypeKeyMap.put("et","application/octet-stream");
        contentTypeKeyMap.put("html","text/html");
        contentTypeKeyMap.put("htm","text/html");
        contentTypeKeyMap.put("ps","application/postscript");
        contentTypeKeyMap.put("sep","application/octet-stream");
        contentTypeKeyMap.put("xsd","application/octet-stream");
        contentTypeKeyMap.put("dps","application/octet-stream");

        contentTypeKeyMap.put("aac","audio/aac");
        contentTypeKeyMap.put("abw","application/x-abiword");
        contentTypeKeyMap.put("arc","application/x-freearc");
        contentTypeKeyMap.put("avi","video/x-msvideo");
        contentTypeKeyMap.put("azw","application/vnd.amazon.ebook");
        contentTypeKeyMap.put("bin","application/octet-stream");
        contentTypeKeyMap.put("bmp","image/bmp");
        contentTypeKeyMap.put("bz","application/x-bzip");
        contentTypeKeyMap.put("bz2","application/x-bzip2");
        contentTypeKeyMap.put("csh","application/x-csh");
        contentTypeKeyMap.put("css","text/css");
        contentTypeKeyMap.put("ceb","application/x-ceb");
        contentTypeKeyMap.put("csv","text/csv");
        contentTypeKeyMap.put("doc","application/msword");
        contentTypeKeyMap.put("dot","application/dot");
        contentTypeKeyMap.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypeKeyMap.put("eot","application/vnd.ms-fontobject");
        contentTypeKeyMap.put("epub","application/epub+zip");
        contentTypeKeyMap.put("gif","image/gif");
        contentTypeKeyMap.put("htm","text/html");
        contentTypeKeyMap.put("ico","image/vnd.microsoft.icon");
        contentTypeKeyMap.put("ics","text/calendar");
        contentTypeKeyMap.put("jar","application/java-archive");
        contentTypeKeyMap.put("jpeg","image/jpeg");
        contentTypeKeyMap.put("jpg","image/jpg");
        contentTypeKeyMap.put("js","text/javascript");
        contentTypeKeyMap.put("json","application/json");
        contentTypeKeyMap.put("jsonld","application/ld+json");
        contentTypeKeyMap.put("mid","audio/midi");
        contentTypeKeyMap.put("mid","audio/x-midi");
        contentTypeKeyMap.put("mp3","audio/mpeg");
        contentTypeKeyMap.put("mpeg","video/mpeg");
        contentTypeKeyMap.put("mpkg","application/vnd.apple.installer+xml");
        contentTypeKeyMap.put("odp","application/vnd.oasis.opendocument.presentation");
        contentTypeKeyMap.put("ods","application/vnd.oasis.opendocument.spreadsheet");
        contentTypeKeyMap.put("odt","application/vnd.oasis.opendocument.text");
        contentTypeKeyMap.put("oga","audio/ogg");
        contentTypeKeyMap.put("ogv","video/ogg");
        contentTypeKeyMap.put("ogx","application/ogg");
        contentTypeKeyMap.put("otf","font/otf");
        contentTypeKeyMap.put("png","image/png");
        contentTypeKeyMap.put("pdf","application/pdf");
        contentTypeKeyMap.put("ofd","application/ofd");
        contentTypeKeyMap.put("ppt","application/vnd.ms-powerpoint");
        contentTypeKeyMap.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
        contentTypeKeyMap.put("rar","application/x-rar-compressed");
        contentTypeKeyMap.put("rtf","application/rtf");
        contentTypeKeyMap.put("rtf","application/msword");
        contentTypeKeyMap.put("sh","application/x-sh");
        contentTypeKeyMap.put("svg","image/svg+xml");
        contentTypeKeyMap.put("swf","application/x-shockwave-flash");
        contentTypeKeyMap.put("tar","application/x-tar");
        contentTypeKeyMap.put("tiff","image/tiff");
        contentTypeKeyMap.put("tif","image/tif");
        contentTypeKeyMap.put("ttf","font/ttf");
        contentTypeKeyMap.put("txt","text/plain");
        contentTypeKeyMap.put("vsd","application/vnd.visio");
        contentTypeKeyMap.put("wav","audio/wav");
        contentTypeKeyMap.put("weba","audio/webm");
        contentTypeKeyMap.put("webm","video/webm");
        contentTypeKeyMap.put("webp","image/webp");
        contentTypeKeyMap.put("wps","application/kswps");
        contentTypeKeyMap.put("wpt","application/kswps");
        contentTypeKeyMap.put("woff","font/woff");
        contentTypeKeyMap.put("woff2","font/woff2");
        contentTypeKeyMap.put("xhtml","application/xhtml+xml");
        contentTypeKeyMap.put("xls","application/vnd.ms-excel");
        contentTypeKeyMap.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypeKeyMap.put("xml","application/xml");
        contentTypeKeyMap.put("xul","application/vnd.mozilla.xul+xml");
        contentTypeKeyMap.put("zip","application/zip");
        contentTypeKeyMap.put("3gp","video/3gpp");
        contentTypeKeyMap.put("edc","application/octet-stream");
        contentTypeKeyMap.put("3g2","video/3gpp2");

        // contentTypeKeyMap.put("3g2","audio/3gpp2");
        // contentTypeKeyMap.put("3gp",".audio/3gpp");
        // contentTypeKeyMap.put("xml","text/xml");
        contentTypeKeyMap.put("7z","application/x-7z-compressed");
        contentTypeKeyMap.put("pfx","application/x-pkcs12");

        contentTypeValueMap.put("audio/aac",".aac");
        contentTypeValueMap.put("application/x-abiword",".abw");
        contentTypeValueMap.put("application/x-freearc",".arc");
        contentTypeValueMap.put("video/x-msvideo",".avi");
        contentTypeValueMap.put("application/vnd.amazon.ebook",".azw");
        contentTypeValueMap.put("application/octet-stream",".bin");
        contentTypeValueMap.put("image/bmp",".bmp");
        contentTypeValueMap.put("image/jpg",".jpg");
        contentTypeValueMap.put("image/jpeg",".jpeg");
        contentTypeValueMap.put("application/x-bzip",".bz");
        contentTypeValueMap.put("application/x-bzip2",".bz2");
        contentTypeValueMap.put("application/x-csh",".csh");
        contentTypeValueMap.put("text/css",".css");
        contentTypeValueMap.put("text/csv",".csv");
        contentTypeValueMap.put("application/msword",".doc");
        contentTypeValueMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document",".docx");
        contentTypeValueMap.put("application/vnd.ms-fontobject",".eot");
        contentTypeValueMap.put("application/epub+zip",".epub");
        contentTypeValueMap.put("application/x-ceb","ceb");
        contentTypeValueMap.put("application/kswps","wps");
        contentTypeValueMap.put("image/gif",".gif");
        contentTypeValueMap.put("text/html",".htm");
        contentTypeValueMap.put("image/vnd.microsoft.icon",".ico");
        contentTypeValueMap.put("text/calendar",".ics");
        contentTypeValueMap.put("application/java-archive",".jar");
        contentTypeValueMap.put("image/jpeg",".jpeg");
        contentTypeValueMap.put("text/javascript",".js");
        contentTypeValueMap.put("application/json",".json");
        contentTypeValueMap.put("application/ld+json",".jsonld");
        contentTypeValueMap.put("audio/midi",".mid");
        contentTypeValueMap.put("audio/x-midi",".mid");
        contentTypeValueMap.put("audio/mpeg",".mp3");
        contentTypeValueMap.put("video/mpeg",".mpeg");
        contentTypeValueMap.put("application/vnd.apple.installer+xml",".mpkg");
        contentTypeValueMap.put("application/vnd.oasis.opendocument.presentation",".odp");
        contentTypeValueMap.put("application/vnd.oasis.opendocument.spreadsheet",".ods");
        contentTypeValueMap.put("application/vnd.oasis.opendocument.text",".odt");
        contentTypeValueMap.put("audio/ogg",".oga");
        contentTypeValueMap.put("video/ogg",".ogv");
        contentTypeValueMap.put("application/ogg",".ogx");
        contentTypeValueMap.put("font/otf",".otf");
        contentTypeValueMap.put("image/png",".png");
        contentTypeValueMap.put("application/pdf",".pdf");
        contentTypeValueMap.put("application/vnd.ms-powerpoint",".ppt");
        contentTypeValueMap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation",".pptx");
        contentTypeValueMap.put("application/x-rar-compressed",".rar");
        contentTypeValueMap.put("application/rtf",".rtf");
        contentTypeValueMap.put("application/x-sh",".sh");
        contentTypeValueMap.put("image/svg+xml",".svg");
        contentTypeValueMap.put("application/x-shockwave-flash",".swf");
        contentTypeValueMap.put("application/x-tar",".tar");
        contentTypeValueMap.put("image/tiff","tiff");
        contentTypeValueMap.put("font/ttf",".ttf");
        contentTypeValueMap.put("text/plain",".txt");
        contentTypeValueMap.put("application/vnd.visio",".vsd");
        contentTypeValueMap.put("audio/wav",".wav");
        contentTypeValueMap.put("audio/webm",".weba");
        contentTypeValueMap.put("video/webm",".webm");
        contentTypeValueMap.put("image/webp",".webp");
        contentTypeValueMap.put("font/woff",".woff");
        contentTypeValueMap.put("font/woff2",".woff2");
        contentTypeValueMap.put("application/xhtml+xml",".xhtml");
        contentTypeValueMap.put("application/vnd.ms-excel",".xls");
        contentTypeValueMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",".xlsx");
        contentTypeValueMap.put("application/xml",".xml");
        contentTypeValueMap.put("text/xml",".xml");
        contentTypeValueMap.put("application/vnd.mozilla.xul+xml",".xul");
        contentTypeValueMap.put("application/zip",".zip");
        contentTypeValueMap.put("video/3gpp",".3gp");
        contentTypeValueMap.put("audio/3gpp",".3gp");
        contentTypeValueMap.put("video/3gpp2",".3g2");
        contentTypeValueMap.put("audio/3gpp2",".3g2");
        contentTypeValueMap.put("application/x-7z-compressed",".7z");
    }

    public MinioHelper(MinioTemplate template, String bucketName, String systemPlatform) {
        this.template = template;
        SysPlatformEnum systemPlatformEnum = SysPlatformEnum.findByCode(systemPlatform);
        switch (systemPlatformEnum) {
            case UNIX:
                this.separator = "/";
                break;
            case WINDOWS:
                this.separator = "\\";
                break;
            default:
        }
    }

    public static MinioHelper init(MinioTemplate template, String bucketName, String systemPlatform) {
        return new MinioHelper(template, bucketName, systemPlatform);
    }

    /**
     * 文件上传
     */
    public MinioObject uploadFile(MultipartFile multipartFile, String bucketName) throws Exception {
        String fileName = multipartFile.getOriginalFilename();
        return this.putMultipartFile( multipartFile,bucketName, fileName);
    }

    /**
     * 文件上传自定义文件名
     */
    public MinioObject uploadFile(MultipartFile multipartFile, String bucketName, String fileName) throws Exception {
        return this.putMultipartFile(multipartFile, bucketName, fileName);
    }

    /**
     * 文件上传
     */
    public MinioObject putMultipartFile(MultipartFile multipartFile, String bucketName, String fileName) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        template.createBucket(bucketName);
        fileName = fileSuffix.concat(this.separator).concat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))).concat(this.separator).concat(fileName);
        template.putFile(bucketName, fileName, multipartFile.getInputStream(), multipartFile.getContentType());
        return new MinioObject(bucketName, fileName, new Date(), multipartFile.getInputStream().available(), null, multipartFile.getContentType());
    }

    /**
     * 文件上传
     */
    public MinioObject uploadFile(File file, String bucketName) throws Exception {
        String fileName = file.getName();
        return this.putFile(file, bucketName, fileName);
    }

    /**
     * 文件上传自定义文件名
     */
    public MinioObject uploadFile(File file, String bucketName, String fileName) throws Exception {
        return this.putFile(file, bucketName, fileName);
    }

    /**
     * 以流的形式上传
     */
    public MinioObject uploadFile(InputStream stream, String bucketName, String fileName) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        template.createBucket(bucketName);
        fileName = this.concatFileName(fileName, fileSuffix);
        template.putFile(bucketName, fileName, stream);
        return new MinioObject(bucketName, fileName, new Date(), stream.available(), null, null);
    }

    /**
     * 获取文件流
     */
    public InputStream getFileInputStream(String bucketName, String fileName) {
        assert bucketName != null;
        assert fileName != null;
        return template.getObject(bucketName, fileName);
    }

    /**
     * 移除文件
     */
    public void removeFile(String bucketName, String fileName) throws Exception {
        template.removeObject(bucketName, fileName);
    }

    /**
     * 上传通过连接共享的文件
     */
    public MinioObject uploadShareLink(String shareLink, String bucketName) throws Exception {
        InputStream urlInputStream = null;
        InputStream resultStream = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(shareLink).openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
            urlInputStream = conn.getInputStream();
            String fileSuffix = contentTypeValueMap.get(conn.getContentType());
            byte[] getData = readInputStream(urlInputStream);
            resultStream = new ByteArrayInputStream(getData);
            return this.uploadFile(resultStream, bucketName,UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix);
        } finally {
            if (null != resultStream) {
                resultStream.close();
            }
            if (null != urlInputStream) {
                urlInputStream.close();
            }
        }
    }

    /**
     * 文件上传
     */
    private MinioObject putFile(File file, String bucketName, String fileName) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        template.createBucket(bucketName);
        InputStream stream = new FileInputStream(file);
        fileName = this.concatFileName(fileName, fileSuffix);
        template.putFile(bucketName, fileName, stream);
        return new MinioObject(bucketName, fileName, new Date(), stream.available(), null, null);
    }

    private String concatFileName (String fileName, String fileSuffix) {
        return fileSuffix.concat(this.separator).concat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))).concat(this.separator).concat(fileName);
    }

    /**
     * 读取流字节
     */
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    // /**
    //  * 获取文件外链并设置有效时长,不设置默认3天
    //  * @param bucketName 桶名称
    //  * @param fileName   文件名称
    //  * @param expires    有效时长(秒)
    //  */
    // public String objectUrl(String bucketName, String fileName, Integer expires) throws Exception {
    //     assert bucketName != null;
    //     assert fileName != null;
    //     if (null == expires) {
    //         expires = 432000;
    //     }
    //     return template.objectUrl(bucketName, fileName, expires, TimeUnit.SECONDS);
    // }
    //
    // /**
    //  * 获取文件外链并设置有效时长3天
    //  */
    // public String getFileUrl(String bucketName, String fileName) throws Exception {
    //     return template.objectUrl(bucketName, fileName);
    // }

}
