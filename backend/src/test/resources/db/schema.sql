CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    daily_goal INT DEFAULT 8,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT '#FF6B6B',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pomodoro_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    tag_id BIGINT,
    task_name VARCHAR(200),
    duration INT DEFAULT 0,
    planned_duration INT DEFAULT 1500,
    status VARCHAR(20),
    started_at DATETIME NOT NULL,
    finished_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    work_duration INT DEFAULT 1500,
    short_break INT DEFAULT 300,
    long_break INT DEFAULT 900,
    long_break_interval INT DEFAULT 4,
    auto_start_break BOOLEAN DEFAULT TRUE,
    auto_start_work BOOLEAN DEFAULT FALSE,
    sound_enabled BOOLEAN DEFAULT TRUE,
    notification_enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
