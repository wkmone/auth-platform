CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE sys_user (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100), phone VARCHAR(20), display_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    last_login_at TIMESTAMP, locked_until TIMESTAMP,
    pwd_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE sys_role (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE, description VARCHAR(200),
    status VARCHAR(20) DEFAULT 'active', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE sys_permission (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) NOT NULL UNIQUE, type VARCHAR(20) NOT NULL,
    path VARCHAR(200), method VARCHAR(10),
    parent_id UUID REFERENCES sys_permission(id),
    sort_order INT DEFAULT 0, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE sys_user_role (
    user_id UUID NOT NULL REFERENCES sys_user(id),
    role_id UUID NOT NULL REFERENCES sys_role(id),
    PRIMARY KEY (user_id, role_id)
);
CREATE TABLE sys_role_permission (
    role_id UUID NOT NULL REFERENCES sys_role(id),
    permission_id UUID NOT NULL REFERENCES sys_permission(id),
    PRIMARY KEY (role_id, permission_id)
);
CREATE TABLE sys_password_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES sys_user(id),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE sys_login_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID, username VARCHAR(50),
    ip_address VARCHAR(50), user_agent VARCHAR(500),
    result VARCHAR(20) NOT NULL, error_detail VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_login_log_created ON sys_login_log(created_at);
INSERT INTO sys_role (id, name, description) VALUES (uuid_generate_v4(), 'super_admin', '超级管理员');
