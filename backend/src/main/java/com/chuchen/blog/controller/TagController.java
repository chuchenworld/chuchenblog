package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Tag;
import com.chuchen.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "标签管理")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }

    @Operation(summary = "获取热门标签")
    @GetMapping("/popular")
    public Result<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(tagService.getPopularTags(limit));
    }
}