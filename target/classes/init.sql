-- 初始化管理员数据
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`, `create_time`)
VALUES (
    'admin',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW' -- BCrypt 加密的 'password'
    ,'admin@example.com',
    'admin',
    1,
    NOW()
);
