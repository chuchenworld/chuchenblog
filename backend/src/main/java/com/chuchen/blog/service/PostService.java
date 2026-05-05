package com.chuchen.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuchen.blog.common.Result;
import com.chuchen.blog.dto.PostDTO;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.entity.User;
import com.chuchen.blog.entity.Tag;
import com.chuchen.blog.mapper.CommentMapper;
import com.chuchen.blog.mapper.PostMapper;
import com.chuchen.blog.mapper.TagMapper;
import com.chuchen.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;

    private static final Pattern MARKDOWN_PATTERN = Pattern.compile("[#*`>\\[\\]()]");

    public Result<Page<Post>> getPosts(Integer pageNum, Integer pageSize, Integer status) {
        Page<Post> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            queryWrapper.eq(Post::getStatus, status);
        }

        queryWrapper.orderByDesc(Post::getCreateTime);
        Page<Post> result = postMapper.selectPage(page, queryWrapper);

        for (Post post : result.getRecords()) {
            if (post.getUserId() != null) {
                User user = userMapper.selectById(post.getUserId());
                if (user != null) {
                    post.setAuthorName(user.getNickname());
                }
            }
            Long count = commentMapper.countByArticleId(post.getId());
            post.setCommentCount(count != null ? count.intValue() : 0);
        }

        return Result.success(result);
    }

    public Result<List<Post>> getAllPublishedPosts() {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getStatus, 1);
        queryWrapper.orderByDesc(Post::getCreateTime);

        List<Post> posts = postMapper.selectList(queryWrapper);

        for (Post post : posts) {
            if (post.getUserId() != null) {
                User user = userMapper.selectById(post.getUserId());
                if (user != null) {
                    post.setAuthorName(user.getNickname());
                }
            }
            Long count = commentMapper.countByArticleId(post.getId());
            post.setCommentCount(count != null ? count.intValue() : 0);
        }

        return Result.success(posts);
    }

    public Result<Post> getPostById(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.error(404, "文章不存在");
        }

        if (post.getUserId() != null) {
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                post.setAuthorName(user.getNickname());
            }
        }

        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        postMapper.updateById(post);

        return Result.success(post);
    }

    public Result<Void> createPost(PostDTO postDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        Post post = new Post();
        String content = postDTO.getContent();
        if (content != null && !content.isEmpty()) {
            if (postDTO.getTitle() == null || postDTO.getTitle().isEmpty()) {
                String title = extractTitle(content);
                post.setTitle(title);
            } else {
                post.setTitle(postDTO.getTitle());
            }

            if (postDTO.getSummary() == null || postDTO.getSummary().isEmpty()) {
                String summary = generateSummary(content);
                post.setSummary(summary);
            } else {
                post.setSummary(postDTO.getSummary());
            }
        } else {
            post.setTitle(postDTO.getTitle());
            post.setSummary(postDTO.getSummary());
        }

        post.setContent(content);
        post.setCoverImage(postDTO.getCoverImage());
        post.setStatus(postDTO.getStatus() == null ? 0 : postDTO.getStatus());
        post.setViewCount(0);
        post.setUserId(user.getId());
        post.setCategoryId(postDTO.getCategoryId());
        post.setCreateTime(LocalDateTime.now());
        post.setTags(postDTO.getTags());

        saveNewTags(postDTO.getTags());

        postMapper.insert(post);
        return Result.success();
    }

    public Result<Void> updatePost(Long id, PostDTO postDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.error(404, "文章不存在");
        }

        if (!"admin".equals(user.getRole()) && !post.getUserId().equals(user.getId())) {
            return Result.error(403, "无权限修改此文章");
        }

        String content = postDTO.getContent();
        if (content != null && !content.isEmpty()) {
            if (postDTO.getTitle() == null || postDTO.getTitle().isEmpty()) {
                String title = extractTitle(content);
                post.setTitle(title);
            } else {
                post.setTitle(postDTO.getTitle());
            }

            if (postDTO.getSummary() == null || postDTO.getSummary().isEmpty()) {
                String summary = generateSummary(content);
                post.setSummary(summary);
            } else {
                post.setSummary(postDTO.getSummary());
            }
        } else {
            post.setTitle(postDTO.getTitle());
            post.setSummary(postDTO.getSummary());
        }

        post.setContent(content);
        post.setCoverImage(postDTO.getCoverImage());
        if (postDTO.getStatus() != null) {
            post.setStatus(postDTO.getStatus());
        }
        if (postDTO.getCategoryId() != null) {
            post.setCategoryId(postDTO.getCategoryId());
        }
        post.setTags(postDTO.getTags());

        saveNewTags(postDTO.getTags());

        postMapper.updateById(post);
        return Result.success();
    }

    private void saveNewTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (String tagName : tags) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue;
            }
            Tag existingTag = tagMapper.selectOne(
                new LambdaQueryWrapper<Tag>().eq(Tag::getName, tagName.trim())
            );
            if (existingTag == null) {
                Tag newTag = new Tag();
                newTag.setName(tagName.trim());
                newTag.setColor("#1f1f1f");
                newTag.setArticleCount(0);
                tagMapper.insert(newTag);
            }
        }
    }

    private String extractTitle(String content) {
        if (content == null || content.isEmpty()) {
            return "无标题";
        }
        String[] lines = content.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("# ")) {
                return line.substring(2).trim();
            }
        }
        return "无标题";
    }

    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String plainText = MARKDOWN_PATTERN.matcher(content).replaceAll(" ");
        plainText = plainText.replaceAll("\\s+|\\n+", " ").trim();
        if (plainText.length() > 100) {
            return plainText.substring(0, 100) + "...";
        }
        return plainText;
    }

    public void cleanOldData() {
        List<Post> posts = postMapper.selectList(null);
        for (Post post : posts) {
            String content = post.getContent();
            if (content != null && !content.isEmpty()) {
                if (post.getTitle() == null || post.getTitle().isEmpty()) {
                    String title = extractTitle(content);
                    post.setTitle(title);
                }
                if (post.getSummary() == null || post.getSummary().isEmpty()) {
                    String summary = generateSummary(content);
                    post.setSummary(summary);
                }
                postMapper.updateById(post);
            }
        }
    }

    public Result<Void> deletePost(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.error(404, "文章不存在");
        }

        if (!"admin".equals(user.getRole()) && !post.getUserId().equals(user.getId())) {
            return Result.error(403, "无权限删除此文章");
        }

        postMapper.deleteById(id);
        return Result.success();
    }

    public Result<List<Post>> getUserPosts(Long userId) {
        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreateTime));

        for (Post post : posts) {
            if (post.getUserId() != null) {
                User user = userMapper.selectById(post.getUserId());
                if (user != null) {
                    post.setAuthorName(user.getNickname());
                }
            }
            Long count = commentMapper.countByArticleId(post.getId());
            post.setCommentCount(count != null ? count.intValue() : 0);
        }

        return Result.success(posts);
    }
}
