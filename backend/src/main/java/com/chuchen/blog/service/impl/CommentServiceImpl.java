package com.chuchen.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Comment;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.entity.SiteConfig;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.event.CommentCreatedEvent;
import com.chuchen.blog.mapper.CommentMapper;
import com.chuchen.blog.mapper.PhotoMapper;
import com.chuchen.blog.mapper.PostMapper;
import com.chuchen.blog.mapper.SiteConfigMapper;
import com.chuchen.blog.mapper.UserMapper;
import com.chuchen.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final PhotoMapper photoMapper;
    private final UserMapper userMapper;
    private final SiteConfigMapper siteConfigMapper;
    private final ApplicationEventPublisher eventPublisher;

    private final Random random = new Random();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Comment> createComment(Long articleId, Long userId, String nickname, String email, String content, Boolean isAnonymous, Boolean notifyAuthor, Long parentId) {
        log.info("【CommentServiceImpl】开始创建评论，文章ID: {}, 用户ID: {}, 匿名: {}, 通知作者: {}", articleId, userId, isAnonymous, notifyAuthor);

        Post post = postMapper.selectById(articleId);
        if (post == null) {
            return Result.error(404, "文章不存在");
        }

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setIsAnonymous(isAnonymous != null && isAnonymous);
        comment.setNotifyAuthor(notifyAuthor != null && notifyAuthor);
        comment.setParentId(parentId);
        comment.setCreateTime(LocalDateTime.now());

        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                comment.setUserId(userId);
                comment.setEmail(email != null && !email.isEmpty() ? email : user.getEmail());

                if (comment.getIsAnonymous()) {
                    String anonymousSuffix = userId != null ? String.valueOf(userId).substring(Math.max(0, String.valueOf(userId).length() - 4)) :
                        String.format("%03d", random.nextInt(1000));
                    comment.setNickname("匿名" + anonymousSuffix);
                } else {
                    comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : user.getNickname());
                }
            }
        } else {
            comment.setUserId(null);
            comment.setEmail(email);

            // 尝试通过 email（匹配 username 字段）查询已注册用户
            if (email != null && !email.isEmpty()) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("username", email);
                User existingUser = userMapper.selectOne(userQueryWrapper);
                if (existingUser != null) {
                    comment.setUserId(existingUser.getId());
                    if (!comment.getIsAnonymous()) {
                        comment.setNickname(existingUser.getNickname());
                    } else {
                        String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                        comment.setNickname("匿名" + anonymousSuffix);
                    }
                } else {
                    // 未查询到用户，保持游客/匿名逻辑
                    if (comment.getIsAnonymous()) {
                        String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                        comment.setNickname("匿名" + anonymousSuffix);
                    } else {
                        comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "匿名游客");
                    }
                }
            } else {
                // email 为空，保持游客/匿名逻辑
                if (comment.getIsAnonymous()) {
                    String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                    comment.setNickname("匿名" + anonymousSuffix);
                } else {
                    comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "匿名游客");
                }
            }
        }

        commentMapper.insert(comment);
        log.info("【CommentServiceImpl】评论创建成功，评论ID: {}", comment.getId());

        if (comment.getNotifyAuthor()) {
            SiteConfig config = siteConfigMapper.selectOne(null);
            String adminEmail = "3349410592@qq.com";
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                adminEmail = config.getConfigValue();
            }

            CommentCreatedEvent event = new CommentCreatedEvent(this, comment, post, adminEmail);
            eventPublisher.publishEvent(event);
            log.info("【CommentServiceImpl】已发布评论创建事件，邮件将在事务提交后异步发送");
        }

        return Result.success(comment);
    }

    @Override
    public Result<List<Comment>> getCommentsByArticleId(Long articleId) {
        log.info("【CommentServiceImpl】获取文章评论列表，文章ID: {}", articleId);
        List<Comment> comments = commentMapper.findByArticleId(articleId);
        return Result.success(comments);
    }

    @Override
    public Result<Void> deleteComment(Long commentId) {
        log.info("【CommentServiceImpl】删除评论，评论ID: {}", commentId);
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        commentMapper.deleteById(commentId);
        log.info("【CommentServiceImpl】评论删除成功");
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Comment> createPhotoComment(Long photoId, Long userId, String nickname, String email, String content, Boolean isAnonymous, Boolean notifyAuthor) {
        log.info("【CommentServiceImpl】开始创建照片评论，照片ID: {}, 用户ID: {}, 匿名: {}, 通知作者: {}", photoId, userId, isAnonymous, notifyAuthor);

        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            return Result.error(404, "照片不存在");
        }

        Comment comment = new Comment();
        comment.setPhotoId(photoId);
        comment.setContent(content);
        comment.setIsAnonymous(isAnonymous != null && isAnonymous);
        comment.setNotifyAuthor(notifyAuthor != null && notifyAuthor);
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0);

        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                comment.setUserId(userId);
                comment.setEmail(email != null && !email.isEmpty() ? email : user.getEmail());

                if (comment.getIsAnonymous()) {
                    String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                    comment.setNickname("匿名" + anonymousSuffix);
                } else {
                    comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : user.getNickname());
                }
            }
        } else {
            comment.setUserId(null);
            comment.setEmail(email);

            // 尝试通过 email（匹配 username 字段）查询已注册用户
            if (email != null && !email.isEmpty()) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("username", email);
                User existingUser = userMapper.selectOne(userQueryWrapper);
                if (existingUser != null) {
                    comment.setUserId(existingUser.getId());
                    if (!comment.getIsAnonymous()) {
                        comment.setNickname(existingUser.getNickname());
                    } else {
                        String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                        comment.setNickname("匿名" + anonymousSuffix);
                    }
                } else {
                    // 未查询到用户，保持游客/匿名逻辑
                    if (comment.getIsAnonymous()) {
                        String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                        comment.setNickname("匿名" + anonymousSuffix);
                    } else {
                        comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "匿名游客");
                    }
                }
            } else {
                // email 为空，保持游客/匿名逻辑
                if (comment.getIsAnonymous()) {
                    String anonymousSuffix = String.format("%03d", random.nextInt(1000));
                    comment.setNickname("匿名" + anonymousSuffix);
                } else {
                    comment.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "匿名游客");
                }
            }
        }

        commentMapper.insert(comment);
        log.info("【CommentServiceImpl】照片评论创建成功，评论ID: {}", comment.getId());

        if (comment.getNotifyAuthor()) {
            SiteConfig config = siteConfigMapper.selectOne(null);
            String adminEmail = "3349410592@qq.com";
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                adminEmail = config.getConfigValue();
            }

            CommentCreatedEvent event = new CommentCreatedEvent(this, comment, photo, adminEmail, true);
            eventPublisher.publishEvent(event);
            log.info("【CommentServiceImpl】已发布照片评论创建事件，邮件将在事务提交后异步发送");
        }

        return Result.success(comment);
    }

    @Override
    public Result<Page<Comment>> getCommentsByPhotoId(Long photoId, int pageNum, int pageSize) {
        log.info("【CommentServiceImpl】获取照片评论列表，照片ID: {}, 页码: {}, 每页数量: {}", photoId, pageNum, pageSize);
        
        Page<Comment> page = new Page<>(pageNum, pageSize);
        List<Comment> comments = commentMapper.findByPhotoId(photoId);
        Long total = commentMapper.countByPhotoId(photoId);
        
        page.setRecords(comments);
        page.setTotal(total);
        
        return Result.success(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likeComment(Long commentId) {
        log.info("【CommentServiceImpl】点赞评论，评论ID: {}", commentId);
        
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        
        commentMapper.incrementLikeCount(commentId);
        log.info("【CommentServiceImpl】评论点赞成功");
        return Result.success();
    }

    @Override
    public Result<List<Comment>> getAllMessages() {
        log.info("【CommentServiceImpl】获取所有留言");
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("article_id").isNull("photo_id").orderByDesc("create_time");
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        log.info("【CommentServiceImpl】获取留言成功，数量: {}", comments.size());
        return Result.success(comments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Comment> createGuestComment(String nickname, String email, String content, Boolean isAnonymous, Long parentId) {
        log.info("【CommentServiceImpl】开始创建游客留言，昵称: {}, 匿名: {}, 父评论ID: {}", nickname, isAnonymous, parentId);

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "留言内容不能为空");
        }

        if (content.length() > 50) {
            return Result.error(400, "留言内容不能超过50字");
        }

        Comment comment = new Comment();
        comment.setContent(content.trim());
        comment.setIsAnonymous(isAnonymous != null && isAnonymous);
        comment.setParentId(parentId);
        comment.setCreateTime(LocalDateTime.now());

        if (comment.getIsAnonymous()) {
            String anonymousSuffix = String.format("%03d", random.nextInt(1000));
            comment.setNickname("匿名" + anonymousSuffix);
            comment.setEmail(null);
        } else {
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "匿名游客";
            }
            comment.setNickname(nickname.trim());
            comment.setEmail(email);
        }

        commentMapper.insert(comment);
        log.info("【CommentServiceImpl】留言创建成功，评论ID: {}", comment.getId());

        return Result.success(comment);
    }
}
