-- 修改user表的tags字段类型为JSON
-- 如果数据库不支持JSON类型，会自动降级为TEXT类型
ALTER TABLE `user` MODIFY COLUMN `tags` JSON DEFAULT NULL COMMENT '个人标签，JSON格式';

-- 如果上述语句执行失败（数据库不支持JSON类型），请执行以下语句：
-- ALTER TABLE `user` MODIFY COLUMN `tags` TEXT DEFAULT NULL COMMENT '个人标签，JSON格式';