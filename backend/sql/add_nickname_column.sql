USE chuchenblog;

-- 为user表添加nickname字段
ALTER TABLE `user` ADD COLUMN `nickname` VARCHAR(50) COMMENT '昵称' AFTER `username`;

-- 为现有数据设置默认值
UPDATE `user` SET `nickname` = `username` WHERE `nickname` IS NULL;

-- 确保nickname字段不为空
ALTER TABLE `user` MODIFY COLUMN `nickname` VARCHAR(50) NOT NULL COMMENT '昵称';
