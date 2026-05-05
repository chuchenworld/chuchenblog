package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Album;
import com.chuchen.blog.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 相册控制器
 */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@Tag(name = "相册管理", description = "相册增删改查接口")
public class AlbumController {

    private final AlbumService albumService;
    private final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @GetMapping
    @Operation(summary = "获取相册列表", description = "获取所有相册列表")
    public Result<List<Album>> getAlbums() {
        logger.info("【AlbumController】开始获取相册列表");
        try {
            Result<List<Album>> result = albumService.getAlbums();
            logger.info("【AlbumController】相册列表获取成功，数量: {}", result.getData() != null ? result.getData().size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("【AlbumController】相册列表获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取相册详情", description = "根据ID获取相册详情")
    public Result<Album> getAlbumById(@PathVariable Long id) {
        logger.info("【AlbumController】开始获取相册详情，ID: {}", id);
        try {
            Result<Album> result = albumService.getAlbumById(id);
            logger.info("【AlbumController】相册详情获取成功，名称: {}", result.getData() != null ? result.getData().getName() : "");
            return result;
        } catch (Exception e) {
            logger.error("【AlbumController】相册详情获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建相册", description = "创建新相册，需要登录")
    public Result<Void> createAlbum(@RequestBody Album album) {
        logger.info("【AlbumController】开始创建相册，名称: {}", album.getName());
        try {
            Result<Void> result = albumService.createAlbum(album);
            logger.info("【AlbumController】相册创建成功");
            return result;
        } catch (Exception e) {
            logger.error("【AlbumController】相册创建失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新相册", description = "更新相册信息，需要登录")
    public Result<Void> updateAlbum(@PathVariable Long id, @RequestBody Album album) {
        logger.info("【AlbumController】开始更新相册，ID: {}, 名称: {}", id, album.getName());
        try {
            Result<Void> result = albumService.updateAlbum(id, album);
            logger.info("【AlbumController】相册更新成功");
            return result;
        } catch (Exception e) {
            logger.error("【AlbumController】相册更新失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除相册", description = "删除相册，仅解除照片关联，不删除照片，需要登录")
    public Result<Void> deleteAlbum(@PathVariable Long id) {
        logger.info("【AlbumController】开始删除相册，ID: {}", id);
        try {
            Result<Void> result = albumService.deleteAlbum(id);
            logger.info("【AlbumController】相册删除成功");
            return result;
        } catch (Exception e) {
            logger.error("【AlbumController】相册删除失败: {}", e.getMessage());
            throw e;
        }
    }
}
