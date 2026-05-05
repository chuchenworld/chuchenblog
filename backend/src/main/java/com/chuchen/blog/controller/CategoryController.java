package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Category;
import com.chuchen.blog.entity.Post;
import com.chuchen.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "分类管理")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }
        return Result.success(category);
    }

    @Operation(summary = "获取分类下的文章")
    @GetMapping("/{id}/posts")
    public Result<List<Post>> getPostsByCategory(@PathVariable Long id) {
        return Result.success(categoryService.getPostsByCategory(id));
    }
}