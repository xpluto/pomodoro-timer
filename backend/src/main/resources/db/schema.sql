-- Pomodoro Timer V2 - MySQL 初始化脚本
-- 使用方法: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS pomodoro
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE pomodoro;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    daily_goal INT NOT NULL DEFAULT 8 COMMENT '每日番茄钟目标数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(7) NOT NULL DEFAULT '#FF6B6B' COMMENT '标签颜色(十六进制)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_tags_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 番茄钟记录表
CREATE TABLE IF NOT EXISTS pomodoro_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    tag_id BIGINT COMMENT '关联标签ID',
    task_name VARCHAR(200) COMMENT '任务名称',
    duration INT NOT NULL DEFAULT 0 COMMENT '实际用时(秒)',
    planned_duration INT NOT NULL DEFAULT 1500 COMMENT '计划时长(秒)',
    status ENUM('completed', 'interrupted', 'abandoned') COMMENT '状态: completed=完成, interrupted=中断, abandoned=放弃',
    started_at DATETIME NOT NULL COMMENT '开始时间',
    finished_at DATETIME COMMENT '结束时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE SET NULL,
    INDEX idx_records_user_started (user_id, started_at),
    INDEX idx_records_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='番茄钟记录表';

-- 用户设置表
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '所属用户ID',
    work_duration INT NOT NULL DEFAULT 1500 COMMENT '工作时长(秒)',
    short_break INT NOT NULL DEFAULT 300 COMMENT '短休息时长(秒)',
    long_break INT NOT NULL DEFAULT 900 COMMENT '长休息时长(秒)',
    long_break_interval INT NOT NULL DEFAULT 4 COMMENT '长休息间隔(轮数)',
    auto_start_break BOOLEAN NOT NULL DEFAULT TRUE COMMENT '工作结束后自动开始休息',
    auto_start_work BOOLEAN NOT NULL DEFAULT FALSE COMMENT '休息结束后自动开始工作',
    sound_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用提示音',
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用浏览器通知',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';
