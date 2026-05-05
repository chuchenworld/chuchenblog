-- 更新数据库表结构
-- 1. 为category表添加description字段
ALTER TABLE category ADD COLUMN description VARCHAR(255) DEFAULT NULL COMMENT '分类描述';

-- 2. 为article表添加category_id字段
ALTER TABLE article ADD COLUMN category_id BIGINT DEFAULT NULL COMMENT '分类ID';
ALTER TABLE article ADD CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES category(id);

-- 3. 为user表添加tags字段
ALTER TABLE user ADD COLUMN tags VARCHAR(500) DEFAULT NULL COMMENT '个人标签，逗号分隔';

-- 4. 如果article_count字段不存在，添加它
ALTER TABLE category ADD COLUMN article_count INT DEFAULT 0 COMMENT '文章数量';