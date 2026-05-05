-- 创建标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    `color` VARCHAR(20) DEFAULT '#1f1f1f' COMMENT '标签颜色',
    `article_count` INT DEFAULT 0 COMMENT '关联文章数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 插入初始标签数据
INSERT IGNORE INTO `tag` (`name`, `color`) VALUES
('Vue', '#42b883'),
('Vue3', '#42b883'),
('React', '#61dafb'),
('JavaScript', '#f7df1e'),
('TypeScript', '#3178c6'),
('Java', '#ed8b00'),
('Spring Boot', '#6db33f'),
('MySQL', '#4479a1'),
('Redis', '#dc382d'),
('Docker', '#2496ed'),
('前端开发', '#667eea'),
('后端开发', '#764ba2'),
('数据库', '#f093fb'),
('运维', '#4facfe'),
('测试', '#43e97b');
