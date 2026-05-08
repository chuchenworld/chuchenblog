package com.chuchen.blog.controller.admin;

import com.chuchen.blog.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@Tag(name = "Upload", description = "文件上传")
public class UploadController {

    @Autowired
    private IFileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileService.upload(file);
            Map<String, Object> result = new HashMap<>();
            result.put("errFiles", new ArrayList<>());
            Map<String, String> succMap = new HashMap<>();
            succMap.put(file.getOriginalFilename(), url);
            result.put("succMap", succMap);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            List<String> errFiles = new ArrayList<>();
            errFiles.add(e.getMessage());
            result.put("errFiles", errFiles);
            result.put("succMap", new HashMap<>());
            return result;
        }
    }
}
