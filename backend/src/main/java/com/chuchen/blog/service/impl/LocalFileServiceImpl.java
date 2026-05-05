package com.chuchen.blog.service.impl;

import com.chuchen.blog.config.FileUploadProperties;
import com.chuchen.blog.service.IFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.upload-type", havingValue = "local")
public class LocalFileServiceImpl implements IFileService {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 创建上传目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filePath = fileUploadProperties.getUploadPath() + "/" + datePath;
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
        return fileUploadProperties.getDomain() + "/uploads/" + datePath + "/" + fileName;
    }
}
