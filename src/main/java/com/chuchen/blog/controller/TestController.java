package com.chuchen.blog.controller;

import com.chuchen.blog.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "测试API文档")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "测试接口", description = "返回Hello World")
    public Result<String> hello() {
        return Result.success("Hello World");
    }
}