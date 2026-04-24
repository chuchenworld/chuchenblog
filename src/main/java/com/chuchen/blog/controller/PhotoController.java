package com.chuchen.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@Tag(name = "摄影管理", description = "摄影作品增删改查接口")
public class PhotoController {

    private final PhotoService photoService;
    private final Logger logger = LoggerFactory.getLogger(PhotoController.class);

    @GetMapping
    @Operation(summary = "获取照片列表", description = "分页获取照片列表")
    public Result<Page<Photo>> getPhotos(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("【PhotoController】开始获取照片列表，页码: {}, 每页数量: {}", pageNum, pageSize);
        try {
            Result<Page<Photo>> result = photoService.getPhotos(pageNum, pageSize);
            logger.info("【PhotoController】照片列表获取成功，总数: {}", result.getData() != null ? result.getData().getTotal() : 0);
            return result;
        } catch (Exception e) {
            logger.error("【PhotoController】照片列表获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取照片详情", description = "根据 ID 获取照片详情")
    public Result<Photo> getPhotoById(@PathVariable Long id) {
        logger.info("【PhotoController】开始获取照片详情，ID: {}", id);
        try {
            Result<Photo> result = photoService.getPhotoById(id);
            logger.info("【PhotoController】照片详情获取成功，名称: {}", result.getData() != null ? result.getData().getName() : "");
            return result;
        } catch (Exception e) {
            logger.error("【PhotoController】照片详情获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建照片", description = "上传新照片，需要登录")
    public Result<Void> createPhoto(@RequestBody Photo photo) {
        logger.info("【PhotoController】开始创建照片，名称: {}", photo.getName());
        try {
            Result<Void> result = photoService.createPhoto(photo);
            logger.info("【PhotoController】照片创建成功");
            return result;
        } catch (Exception e) {
            logger.error("【PhotoController】照片创建失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新照片", description = "更新照片信息，需要登录")
    public Result<Void> updatePhoto(@PathVariable Long id, @RequestBody Photo photo) {
        logger.info("【PhotoController】开始更新照片，ID: {}, 名称: {}", id, photo.getName());
        try {
            Result<Void> result = photoService.updatePhoto(id, photo);
            logger.info("【PhotoController】照片更新成功");
            return result;
        } catch (Exception e) {
            logger.error("【PhotoController】照片更新失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除照片", description = "删除照片，需要登录")
    public Result<Void> deletePhoto(@PathVariable Long id) {
        logger.info("【PhotoController】开始删除照片，ID: {}", id);
        try {
            Result<Void> result = photoService.deletePhoto(id);
            logger.info("【PhotoController】照片删除成功");
            return result;
        } catch (Exception e) {
            logger.error("【PhotoController】照片删除失败: {}", e.getMessage());
            throw e;
        }
    }
}
