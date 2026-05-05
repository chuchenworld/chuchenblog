package com.chuchen.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Comment;

import java.util.List;

public interface CommentService {
    Result<Comment> createComment(Long articleId, Long userId, String nickname, String email, String content, Boolean isAnonymous, Boolean notifyAuthor, Long parentId);
    Result<List<Comment>> getCommentsByArticleId(Long articleId);
    Result<Void> deleteComment(Long commentId);

    // 照片评论相关方法
    Result<Comment> createPhotoComment(Long photoId, Long userId, String nickname, String email, String content, Boolean isAnonymous, Boolean notifyAuthor);
    Result<Page<Comment>> getCommentsByPhotoId(Long photoId, int pageNum, int pageSize);
    Result<Void> likeComment(Long commentId);

    // 留言板相关方法
    Result<List<Comment>> getAllMessages();
    Result<Comment> createGuestComment(String nickname, String email, String content, Boolean isAnonymous, Long parentId);
}