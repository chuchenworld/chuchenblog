USE chuchenblog;

-- 修改category表，添加description字段
ALTER TABLE `category` ADD COLUMN `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述' AFTER `name`;

-- 为article表添加category_id字段
ALTER TABLE `article` ADD COLUMN `category_id` BIGINT COMMENT '分类ID' AFTER `user_id`;

-- 为user表添加tags字段（JSON格式存储个人标签）
ALTER TABLE `user` ADD COLUMN `tags` VARCHAR(500) DEFAULT NULL COMMENT '个人标签，JSON格式' AFTER `bio`;

-- 创建索引
ALTER TABLE `article` ADD INDEX `idx_category_id` (`category_id`);

-- 初始化一些默认分类（如果表为空）
INSERT INTO `category` (`name`, `description`, `article_count`) VALUES
('前端开发', '前端技术相关文章', 0),
('后端开发', '后端技术相关文章', 0),
('DevOps', 'DevOps相关文章', 0),
('Life', '生活随笔', 0),
('Others', '其他分类', 0)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);
