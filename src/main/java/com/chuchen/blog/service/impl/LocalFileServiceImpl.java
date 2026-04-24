package com.chuchen.blog.service.impl;

import com.chuchen.blog.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service("localFileService")
@Slf4j
public class LocalFileServiceImpl implements IFileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.domain}")
    private String domain;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 创建上传目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filePath = uploadPath + "/" + datePath;
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("文件名为空");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        // 保存文件
        File targetFile = new File(filePath + "/" + fileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }

        // 返回文件URL
        return domain + "/uploads/" + datePath + "/" + fileName;
    }
}
