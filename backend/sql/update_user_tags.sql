-- 更新user表的tags字段类型为JSON
ALTER TABLE `user` MODIFY COLUMN `tags` JSON DEFAULT NULL COMMENT '个人标签，JSON格式';

-- 如果数据库不支持JSON类型，执行以下语句：
-- ALTER TABLE `user` MODIFY COLUMN `tags` TEXT DEFAULT NULL COMMENT '个人标签，JSON格式';

-- 为测试用户添加一些标签
UPDATE `user` SET `tags` = '["Vue", "Java", "Spring Boot", "前端开发", "后端开发"]' WHERE `id` = 1;
