-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS user_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE user_management;

-- 创建biz_users表，用于存储用户登录和权限信息
CREATE TABLE IF NOT EXISTS biz_users (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    permission_level TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '权限级别：1-普通用户，2-管理员，3-超级管理员',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户状态：1-正常，0-禁用，2-锁定',
    register_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_time TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    INDEX idx_register_time (register_time),
    INDEX idx_status_permission (status, permission_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表';

-- 创建biz_user_info表，用于存储用户详细个人信息
CREATE TABLE IF NOT EXISTS biz_user_info (
    info_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '信息ID，主键',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID，外键',
    real_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '真实姓名',
    phone VARCHAR(20) NULL DEFAULT NULL COMMENT '手机号码',
    email VARCHAR(100) NULL DEFAULT NULL COMMENT '电子邮箱',
    address VARCHAR(255) NULL DEFAULT NULL COMMENT '地址',
    birth_date DATE NULL DEFAULT NULL COMMENT '出生日期',
    gender CHAR(1) NULL DEFAULT NULL COMMENT '性别：M-男，F-女，U-未知',
    avatar_url VARCHAR(500) NULL DEFAULT NULL COMMENT '头像URL',
    seat_number VARCHAR(20) NULL DEFAULT NULL COMMENT '座位号/工位号',
    department VARCHAR(100) NULL DEFAULT NULL COMMENT '所属部门/实体',
    direct_leader_id INT UNSIGNED NULL DEFAULT NULL COMMENT '直属领导用户ID',
    position VARCHAR(100) NULL DEFAULT NULL COMMENT '职位',
    hire_date DATE NULL DEFAULT NULL COMMENT '入职日期',
    team VARCHAR(100) NULL DEFAULT NULL COMMENT '所属组/团队',
    status TINYINT(1) DEFAULT 1 COMMENT '信息状态：1-正常，0-离职',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (info_id),
    UNIQUE KEY uk_user_id (user_id), -- 确保一个用户只有一条信息记录
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email),
    INDEX idx_real_name (real_name),
    INDEX idx_department (department),
    INDEX idx_position (position),
    INDEX idx_team (team),
    INDEX idx_work_status (status),
    INDEX idx_hire_date (hire_date),
    INDEX idx_direct_leader (direct_leader_id),
    INDEX idx_seat_number (seat_number),
    CONSTRAINT fk_biz_user_info_user
     FOREIGN KEY (user_id)
         REFERENCES biz_users (user_id)
         ON DELETE CASCADE
         ON UPDATE CASCADE,
    CONSTRAINT fk_biz_user_info_direct_leader
     FOREIGN KEY (direct_leader_id)
         REFERENCES biz_users (user_id)
         ON DELETE SET NULL
         ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详细信息表';

-- 可选：创建部门表（如果需要规范部门数据）
CREATE TABLE IF NOT EXISTS department (
    department_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    department_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id INT UNSIGNED NULL DEFAULT NULL COMMENT '上级部门ID',
    manager_id INT UNSIGNED NULL DEFAULT NULL COMMENT '部门经理用户ID',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (department_id),
    UNIQUE KEY uk_department_name (department_name),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    CONSTRAINT fk_department_parent
        FOREIGN KEY (parent_id)
            REFERENCES department (department_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,
    CONSTRAINT fk_department_manager
        FOREIGN KEY (manager_id)
            REFERENCES biz_users (user_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门信息表';

-- 借款人信息表（主表）
CREATE TABLE IF NOT EXISTS borrowers (
    borrower_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '借款人ID，主键',
    user_id INT UNSIGNED NULL DEFAULT NULL COMMENT '关联用户ID，外键',
    name VARCHAR(100) NOT NULL COMMENT '借款人姓名',
    tel VARCHAR(255) NULL DEFAULT NULL COMMENT '联系电话',
    start_date DATE NULL DEFAULT NULL COMMENT '借款开始日期',
    end_date DATE NULL DEFAULT NULL COMMENT '清账日期',
    total_loan DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '总借款额',
    total_interest DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '总利息额',
    remaining_balance DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '剩余还款额',
    total_amount DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '总金额（包括本金和利息）',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (borrower_id),
    INDEX idx_user_id (user_id),
    INDEX idx_name (name),
    INDEX idx_tel (tel),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_remaining_balance (remaining_balance),
    INDEX idx_created_time (created_time),
    INDEX idx_updated_time (updated_time),
    CONSTRAINT fk_borrowers_user
     FOREIGN KEY (user_id)
         REFERENCES biz_users (user_id)
         ON DELETE SET NULL
         ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借款人信息表';

-- 借款交易详情表（从表）
CREATE TABLE IF NOT EXISTS borrower_details (
    detail_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易ID，主键',
    borrower_id INT UNSIGNED NOT NULL COMMENT '借款人ID，外键',
    transaction_type VARCHAR(20) NOT NULL COMMENT '交易类型：借款、还款、利息',
    amount DECIMAL(15, 2) NOT NULL COMMENT '交易金额',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    notes TEXT NULL DEFAULT NULL COMMENT '备注说明',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (detail_id),
    INDEX idx_borrower_id (borrower_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_amount (amount),
    INDEX idx_created_time (created_time),
    INDEX idx_updated_time (updated_time),
    CONSTRAINT fk_borrower_details_borrower
        FOREIGN KEY (borrower_id)
            REFERENCES borrowers (borrower_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借款交易详情表';

-- 创建视图用于常用查询
CREATE OR REPLACE VIEW v_user_details AS
SELECT
    u.user_id,
    u.username,
    u.permission_level,
    u.status as account_status,
    u.register_time,
    u.last_login_time,
    ui.real_name,
    ui.phone,
    ui.email,
    ui.address,
    ui.seat_number,
    ui.department,
    ui.direct_leader_id,
    (SELECT real_name FROM biz_user_info WHERE user_id = ui.direct_leader_id) as direct_leader_name,
    ui.position,
    ui.hire_date,
    ui.team,
    ui.status,
    ui.gender,
    ui.birth_date
FROM biz_users u
    LEFT JOIN biz_user_info ui ON u.user_id = ui.user_id;

-- 创建视图：借款人汇总信息
CREATE OR REPLACE VIEW v_borrower_summary AS
SELECT
    b.borrower_id,
    b.user_id,
    u.username,
    ui.real_name as user_real_name,
    b.name,
    b.tel,
    b.start_date,
    b.end_date,
    b.total_loan,
    b.total_interest,
    b.remaining_balance,
    b.total_amount,
    b.created_time,
    b.updated_time,
    -- 计算已还金额
    COALESCE(b.total_amount - b.remaining_balance, 0) as paid_amount,
    -- 计算还款进度
    CASE
        WHEN b.total_amount > 0
            THEN ROUND((COALESCE(b.total_amount - b.remaining_balance, 0) / b.total_amount) * 100, 2)
        ELSE 0
        END as repayment_percentage,
    -- 借款状态
    CASE
        WHEN b.remaining_balance <= 0 THEN '已结清'
        WHEN b.end_date < CURDATE() THEN '逾期'
        ELSE '未结清'
        END as loan_status,
    -- 最近交易日期
    (SELECT MAX(transaction_date)
     FROM borrower_details
     WHERE borrower_id = b.borrower_id) as last_transaction_date
FROM borrowers b
    LEFT JOIN biz_users u ON b.user_id = u.user_id
    LEFT JOIN biz_user_info ui ON u.user_id = ui.user_id;

-- 创建视图：交易明细汇总
CREATE OR REPLACE VIEW v_transaction_details AS
SELECT
    bd.detail_id,
    bd.borrower_id,
    b.name as borrower_name,
    b.tel,
    bd.transaction_type,
    bd.amount,
    bd.transaction_date,
    bd.notes,
    bd.created_time,
    bd.updated_time,
    -- 累计到该交易的金额
    (SELECT SUM(amount)
     FROM borrower_details
     WHERE borrower_id = bd.borrower_id
       AND transaction_date <= bd.transaction_date) as cumulative_amount
FROM borrower_details bd
     JOIN borrowers b ON bd.borrower_id = b.borrower_id;