package com.chuchen.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostDTO {
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Integer status;
    private Long categoryId;
    private List<String> tags;
}