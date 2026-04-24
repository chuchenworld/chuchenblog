package com.chuchen.blog.config;

import com.chuchen.blog.service.IFileService;
import com.chuchen.blog.service.impl.LocalFileServiceImpl;
import com.chuchen.blog.service.impl.OssFileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfig {

    @Value("${file.upload-type}")
    private String uploadType;

    @Bean
    public IFileService fileService() {
        if ("oss".equalsIgnoreCase(uploadType)) {
            return new OssFileServiceImpl();
        } else {
            return new LocalFileServiceImpl();
        }
    }
}
