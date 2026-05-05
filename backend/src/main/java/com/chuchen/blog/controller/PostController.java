package com.chuchen.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.PostDTO;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "文章管理", description = "文章增删改查接口")
public class PostController {

    private final PostService postService;
    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    @GetMapping
    @Operation(summary = "获取文章列表", description = "分页获取文章，status 参数用于前台筛选已发布文章，管理后台不传此参数")
    public Result<Page<Post>> getPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        logger.info("【PostController】开始获取文章列表，页码: {}, 每页数量: {}, 状态筛选: {}", pageNum, pageSize, status);
        try {
            Result<Page<Post>> result = postService.getPosts(pageNum, pageSize, status);
            logger.info("【PostController】文章列表获取成功，总数: {}", result.getData() != null ? result.getData().getTotal() : 0);
            return result;
        } catch (Exception e) {
            logger.error("【PostController】文章列表获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有文章", description = "获取所有已发布的文章，不分页，用于前台筛选")
    public Result<List<Post>> getAllPosts() {
        logger.info("【PostController】开始获取所有文章");
        try {
            Result<List<Post>> result = postService.getAllPublishedPosts();
            logger.info("【PostController】所有文章获取成功，数量: {}", result.getData() != null ? result.getData().size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("【PostController】所有文章获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情", description = "根据 ID 获取文章，增加阅读次数")
    public Result<Post> getPostById(@PathVariable Long id) {
        logger.info("【PostController】开始获取文章详情，ID: {}", id);
        try {
            Result<Post> result = postService.getPostById(id);
            logger.info("【PostController】文章详情获取成功，标题: {}", result.getData() != null ? result.getData().getTitle() : "");
            return result;
        } catch (Exception e) {
            logger.error("【PostController】文章详情获取失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建文章", description = "创建新文章，需要登录")
    public Result<Void> createPost(@RequestBody PostDTO postDTO) {
        logger.info("【PostController】开始创建文章，标题: {}", postDTO.getTitle());
        try {
            Result<Void> result = postService.createPost(postDTO);
            logger.info("【PostController】文章创建成功");
            return result;
        } catch (Exception e) {
            logger.error("【PostController】文章创建失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新文章", description = "更新文章，仅作者或管理员可操作")
    public Result<Void> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        logger.info("【PostController】开始更新文章，ID: {}, 标题: {}", id, postDTO.getTitle());
        try {
            Result<Void> result = postService.updatePost(id, postDTO);
            logger.info("【PostController】文章更新成功");
            return result;
        } catch (Exception e) {
            logger.error("【PostController】文章更新失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除文章", description = "删除文章，仅作者或管理员可操作")
    public Result<Void> deletePost(@PathVariable Long id) {
        logger.info("【PostController】开始删除文章，ID: {}", id);
        try {
            Result<Void> result = postService.deletePost(id);
            logger.info("【PostController】文章删除成功");
            return result;
        } catch (Exception e) {
            logger.error("【PostController】文章删除失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户文章列表", description = "获取指定用户的所有文章")
    public Result<List<Post>> getUserPosts(@PathVariable Long userId) {
        logger.info("【PostController】开始获取用户文章列表，用户ID: {}", userId);
        try {
            Result<List<Post>> result = postService.getUserPosts(userId);
            logger.info("【PostController】用户文章列表获取成功，数量: {}", result.getData() != null ? result.getData().size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("【PostController】用户文章列表获取失败: {}", e.getMessage());
            throw e;
        }
    }
}