package com.chuchen.blog.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String account;
    private String password;
}