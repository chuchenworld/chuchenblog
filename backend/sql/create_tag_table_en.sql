CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `color` VARCHAR(20) DEFAULT '#1f1f1f',
    `article_count` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `tag` (`name`, `color`) VALUES
('Vue', '#42b883'),
('Vue3', '#42b883'),
('React', '#61dafb'),
('JavaScript', '#f7df1e'),
('TypeScript', '#3178c6'),
('Java', '#ed8b00'),
('Spring Boot', '#6db33f'),
('Frontend', '#667eea'),
('Backend', '#764ba2'),
('Database', '#f093fb');
