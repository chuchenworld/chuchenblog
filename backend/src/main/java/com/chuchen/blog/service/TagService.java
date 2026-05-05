package com.chuchen.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.entity.Tag;
import com.chuchen.blog.mapper.PostMapper;
import com.chuchen.blog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagService extends ServiceImpl<TagMapper, Tag> {

    private final TagMapper tagMapper;
    private final PostMapper postMapper;

    public List<Tag> getAllTags() {
        List<Tag> tags = tagMapper.selectList(
            new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId)
        );

        recalculateTagCounts(tags);

        return tags;
    }

    public List<Tag> getPopularTags(int limit) {
        List<Tag> tags = tagMapper.selectList(
            new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getArticleCount)
                .last("LIMIT " + limit)
        );

        recalculateTagCounts(tags);

        return tags;
    }

    private void recalculateTagCounts(List<Tag> tags) {
        List<Post> allPosts = postMapper.selectList(
            new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1)
        );

        Map<String, Integer> tagCountMap = new HashMap<>();

        for (Post post : allPosts) {
            if (post.getTags() != null) {
                for (String tagName : post.getTags()) {
                    tagCountMap.put(tagName, tagCountMap.getOrDefault(tagName, 0) + 1);
                }
            }
        }

        for (Tag tag : tags) {
            tag.setArticleCount(tagCountMap.getOrDefault(tag.getName(), 0));
        }
    }

    public void recalculateAllTagCounts() {
        List<Tag> allTags = tagMapper.selectList(null);
        List<Post> allPosts = postMapper.selectList(
            new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1)
        );

        Map<String, Integer> tagCountMap = new HashMap<>();

        for (Post post : allPosts) {
            if (post.getTags() != null) {
                for (String tagName : post.getTags()) {
                    tagCountMap.put(tagName, tagCountMap.getOrDefault(tagName, 0) + 1);
                }
            }
        }

        for (Tag tag : allTags) {
            Integer count = tagCountMap.getOrDefault(tag.getName(), 0);
            tag.setArticleCount(count);
            tagMapper.updateById(tag);
        }
    }

    public Tag getTagById(Long id) {
        return tagMapper.selectById(id);
    }

    public Tag getTagByName(String name) {
        return tagMapper.selectOne(
            new LambdaQueryWrapper<Tag>().eq(Tag::getName, name)
        );
    }

    public void createTag(Tag tag) {
        tag.setArticleCount(0);
        tagMapper.insert(tag);
    }

    public void updateTag(Tag tag) {
        tagMapper.updateById(tag);
    }

    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }
}