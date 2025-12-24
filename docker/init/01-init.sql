-- 初始化数据库脚本
-- 确保数据库字符集正确
ALTER DATABASE debt_bills CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权（如果不存在）
CREATE USER IF NOT EXISTS 'jasper_user'@'%' IDENTIFIED BY 'jasper_pass';
GRANT ALL PRIVILEGES ON debt_bills.* TO 'jasper_user'@'%';
FLUSH PRIVILEGES;