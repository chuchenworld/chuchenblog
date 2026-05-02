// src/main/java/com/chuchen/blog/config/FileUploadProperties.java
package com.chuchen.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileUploadProperties {

    /**
     * 上传类型：local 或 oss
     */
    private String uploadType = "local";

    /**
     * 本地上传路径
     */
    private String uploadPath = "/tmp/uploads";

    /**
     * 访问域名
     */
    private String domain = "";
}
