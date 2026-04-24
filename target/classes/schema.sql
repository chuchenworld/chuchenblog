CREATE DATABASE IF NOT EXISTS chuchenblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE chuchenblog;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色（admin/user）',
    `status` INT DEFAULT 1 COMMENT '状态（1:正常 0:禁用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章 ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `summary` VARCHAR(500) COMMENT '摘要',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `status` INT DEFAULT 0 COMMENT '状态（0:草稿 1:发布）',
    `view_count` INT DEFAULT 0 COMMENT '阅读次数',
    `user_id` BIGINT NOT NULL COMMENT '作者 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `tags` JSON COMMENT '标签',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';