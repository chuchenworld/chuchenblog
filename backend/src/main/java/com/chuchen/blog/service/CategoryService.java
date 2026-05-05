package com.chuchen.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuchen.blog.entity.Category;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.mapper.CategoryMapper;
import com.chuchen.blog.mapper.CommentMapper;
import com.chuchen.blog.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    private final CategoryMapper categoryMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public List<Category> getAllCategories() {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getId)
        );

        for (Category category : categories) {
            Integer count = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getCategoryId, category.getId())
            ).intValue();
            category.setArticleCount(count);
        }

        return categories;
    }

    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category != null) {
            Integer count = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getCategoryId, id)
            ).intValue();
            category.setArticleCount(count);
        }
        return category;
    }

    public List<Post> getPostsByCategory(Long categoryId) {
        List<Post> posts = postMapper.selectList(
            new LambdaQueryWrapper<Post>()
                .eq(Post::getCategoryId, categoryId)
                .orderByDesc(Post::getCreateTime)
        );

        for (Post post : posts) {
            Long count = commentMapper.countByArticleId(post.getId());
            post.setCommentCount(count != null ? count.intValue() : 0);
        }

        return posts;
    }

    @Transactional
    public Category createCategory(Category category) {
        category.setArticleCount(0);
        categoryMapper.insert(category);
        return category;
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            return null;
        }
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        categoryMapper.updateById(existing);
        return existing;
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        categoryMapper.deleteById(id);
        return true;
    }

    public void recalculateCategoryCounts() {
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            Integer count = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getCategoryId, category.getId())
            ).intValue();
            category.setArticleCount(count);
            categoryMapper.updateById(category);
        }
    }
}