package com.chuchen.blog.controller.admin;

import com.chuchen.blog.common.Result;
import com.chuchen.blog.entity.Tag;
import com.chuchen.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "AdminTag", description = "管理端标签管理") public class AdminTagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            return Result.error(404, "标签不存在");
        }
        return Result.success(tag);
    }

    @Operation(summary = "创建标签")
    @PostMapping
    public Result<Void> createTag(@RequestBody Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            return Result.error(400, "标签名称不能为空");
        }
        Tag existingTag = tagService.getTagByName(tag.getName().trim());
        if (existingTag != null) {
            return Result.error(400, "标签名称已存在");
        }
        tag.setName(tag.getName().trim());
        tagService.createTag(tag);
        return Result.success();
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Long id, @RequestBody Tag tagUpdate) {
        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            return Result.error(404, "标签不存在");
        }
        if (tagUpdate.getName() != null && !tagUpdate.getName().trim().isEmpty()) {
            Tag existingTag = tagService.getTagByName(tagUpdate.getName().trim());
            if (existingTag != null && !existingTag.getId().equals(id)) {
                return Result.error(400, "标签名称已存在");
            }
            tag.setName(tagUpdate.getName().trim());
        }
        if (tagUpdate.getColor() != null) {
            tag.setColor(tagUpdate.getColor());
        }
        tagService.updateTag(tag);
        return Result.success();
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            return Result.error(404, "标签不存在");
        }
        tagService.deleteTag(id);
        return Result.success();
    }

    @Operation(summary = "重新计算所有标签的文章数量")
    @PostMapping("/recalculate")
    public Result<Void> recalculateTagCounts() {
        tagService.recalculateAllTagCounts();
        return Result.success();
    }
}