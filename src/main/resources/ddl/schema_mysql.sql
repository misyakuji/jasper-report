-- 创建borrowers表，用于存储借款人信息
CREATE TABLE IF NOT EXISTS borrowers (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增ID',
    name VARCHAR(100) NOT NULL COMMENT '借款人姓名',
    tel VARCHAR(255) COMMENT '联系电话',
    start_date DATE COMMENT '借款开始日期',
    end_date DATE COMMENT '清账日期（如果已清账，则为最后还款日期；否则为空）',
    total_loan DECIMAL(10, 2) COMMENT '总借款额',
    total_interest DECIMAL(10, 2) COMMENT '总利息额',
    remaining_balance DECIMAL(10, 2) COMMENT '剩余还款额',
    total_amount DECIMAL(10, 2) COMMENT '总金额（包括本金和利息）',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_start_date (start_date),
    INDEX idx_remaining_balance (remaining_balance)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借款人信息表';

-- 创建borrower_details表，用于记录所有与借款相关的交易细节
CREATE TABLE IF NOT EXISTS borrower_details (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增ID',
    borrower_id INT NOT NULL COMMENT '外键，关联到borrowers表的id',
    transaction_type ENUM('Loan', 'Repayment', 'Interest') NOT NULL COMMENT '交易类型：Loan-借款，Repayment-还款，Interest-利息',
    amount DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    notes TEXT COMMENT '其他备注或说明',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE,
    INDEX idx_borrower_id (borrower_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_amount (amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借款交易详情表';

-- 创建users表，用于存储用户登录和权限信息
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名，登录账号',
    password VARCHAR(100) NOT NULL COMMENT '密码，BCrypt加密存储',
    role ENUM('ROLE_ADMIN', 'ROLE_USER', 'ROLE_MANAGER', 'ROLE_GUEST') NOT NULL DEFAULT 'ROLE_USER' COMMENT '用户角色：ROLE_ADMIN-管理员，ROLE_USER-普通用户，ROLE_MANAGER-经理，ROLE_GUEST-访客',
    enabled TINYINT(1) DEFAULT 1 COMMENT '账户状态：1-启用，0-禁用',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username) COMMENT '用户名唯一约束',
    INDEX idx_role (role) COMMENT '角色索引',
    INDEX idx_enabled (enabled) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表';

-- 创建user_info表，用于存储用户详细个人信息
CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID，外键',
    real_name VARCHAR(50) COMMENT '真实姓名',
    mobile VARCHAR(20) COMMENT '手机号码',
    phone VARCHAR(20) COMMENT '固定电话',
    email VARCHAR(100) COMMENT '电子邮箱',
    seat VARCHAR(50) COMMENT '座位号',
    entity VARCHAR(100) COMMENT '所属实体/部门',
    leader VARCHAR(50) COMMENT '直属领导',
    position VARCHAR(50) COMMENT '职位',
    join_day DATE COMMENT '入职日期',
    `group` VARCHAR(200) COMMENT '所属组/团队',
    avatar_url VARCHAR(255) COMMENT '头像URL地址',
    status TINYINT(1) DEFAULT 1 COMMENT '信息状态：1-正常，0-离职',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_mobile (mobile) COMMENT '手机号索引',
    INDEX idx_email (email) COMMENT '邮箱索引',
    INDEX idx_entity (entity) COMMENT '部门索引',
    INDEX idx_position (position) COMMENT '职位索引',
    INDEX idx_status (status) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详细信息表';

