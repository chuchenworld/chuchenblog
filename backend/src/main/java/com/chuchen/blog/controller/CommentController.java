package com.chuchen.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Comment;
import com.chuchen.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "评论相关接口")
public class CommentController {

    private final CommentService commentService;
    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @GetMapping("/article/{articleId}")
    @Operation(summary = "获取文章评论列表", description = "根据文章ID获取所有评论")
    public Result<List<Comment>> getCommentsByArticleId(@PathVariable Long articleId) {
        logger.info("【CommentController】获取文章评论列表，文章ID: {}", articleId);
        return commentService.getCommentsByArticleId(articleId);
    }

    @PostMapping
    @Operation(summary = "提交评论", description = "提交新评论，支持匿名评论")
    public Result<Comment> createComment(@RequestBody Map<String, Object> params) {
        Long articleId = parseLong(params.get("articleId"));
        String content = (String) params.get("content");
        String nickname = (String) params.get("nickname");
        String email = (String) params.get("email");
        Boolean isAnonymous = params.get("isAnonymous") != null ? (Boolean) params.get("isAnonymous") : false;
        Boolean notifyAuthor = params.get("notifyAuthor") != null ? (Boolean) params.get("notifyAuthor") : false;
        Long parentId = params.get("parentId") != null ? parseLong(params.get("parentId")) : null;

        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                userId = Long.parseLong(auth.getName());
            } catch (NumberFormatException e) {
                logger.warn("【CommentController】解析用户ID失败: {}", e.getMessage());
            }
        }

        logger.info("【CommentController】提交评论，文章ID: {}, 用户ID: {}, 匿名: {}, 通知作者: {}", articleId, userId, isAnonymous, notifyAuthor);
        return commentService.createComment(articleId, userId, nickname, email, content, isAnonymous, notifyAuthor, parentId);
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("【CommentController】解析Long失败: {}", value);
            return null;
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除评论", description = "删除评论，需要管理员权限")
    public Result<Void> deleteComment(@PathVariable Long id) {
        logger.info("【CommentController】删除评论，评论ID: {}", id);
        return commentService.deleteComment(id);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有留言", description = "获取留言板的所有留言")
    public Result<List<Comment>> getAllMessages() {
        logger.info("【CommentController】获取所有留言");
        return commentService.getAllMessages();
    }

    @PostMapping("/guest")
    @Operation(summary = "游客提交留言", description = "游客或匿名用户提交留言")
    public Result<Comment> createGuestComment(@RequestBody Map<String, Object> params) {
        String nickname = (String) params.get("nickname");
        String email = (String) params.get("email");
        String content = (String) params.get("content");
        Boolean isAnonymous = params.get("isAnonymous") != null ? (Boolean) params.get("isAnonymous") : false;
        Long parentId = params.get("parentId") != null ? parseLong(params.get("parentId")) : null;

        logger.info("【CommentController】游客提交留言，昵称: {}, 匿名: {}, 父评论ID: {}", nickname, isAnonymous, parentId);
        return commentService.createGuestComment(nickname, email, content, isAnonymous, parentId);
    }

    @GetMapping("/photo/{photoId}")
    @Operation(summary = "获取照片评论列表", description = "根据照片ID获取所有评论，支持分页")
    public Result<Page<Comment>> getCommentsByPhotoId(
            @PathVariable Long photoId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        logger.info("【CommentController】获取照片评论列表，照片ID: {}, 页码: {}, 每页数量: {}", photoId, pageNum, pageSize);
        return commentService.getCommentsByPhotoId(photoId, pageNum, pageSize);
    }

    @PostMapping("/photo")
    @Operation(summary = "提交照片评论", description = "提交新照片评论，支持匿名评论")
    public Result<Comment> createPhotoComment(@RequestBody Map<String, Object> params) {
        Long photoId = parseLong(params.get("photoId"));
        String content = (String) params.get("content");
        String nickname = (String) params.get("nickname");
        String email = (String) params.get("email");
        Boolean isAnonymous = params.get("isAnonymous") != null ? (Boolean) params.get("isAnonymous") : false;
        Boolean notifyAuthor = params.get("notifyAuthor") != null ? (Boolean) params.get("notifyAuthor") : false;

        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                userId = Long.parseLong(auth.getName());
            } catch (NumberFormatException e) {
                logger.warn("【CommentController】解析用户ID失败: {}", e.getMessage());
            }
        }

        logger.info("【CommentController】提交照片评论，照片ID: {}, 用户ID: {}, 匿名: {}, 通知作者: {}", photoId, userId, isAnonymous, notifyAuthor);
        return commentService.createPhotoComment(photoId, userId, nickname, email, content, isAnonymous, notifyAuthor);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞评论", description = "对评论进行点赞")
    public Result<Void> likeComment(@PathVariable Long id) {
        logger.info("【CommentController】点赞评论，评论ID: {}", id);
        return commentService.likeComment(id);
    }
}
