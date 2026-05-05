package com.chuchen.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    /**
     * 上传文件
     * @param file 文件
     * @return 文件URL
     */
    String upload(MultipartFile file);
}
