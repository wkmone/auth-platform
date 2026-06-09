CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE notification_template (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject_template VARCHAR(500),
    body_template TEXT NOT NULL,
    variables JSONB DEFAULT '[]',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_message (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID REFERENCES notification_template(id),
    recipient VARCHAR(500) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(500),
    body TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    error_detail TEXT,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_msg_status ON notification_message(status);
CREATE INDEX idx_msg_created ON notification_message(created_at DESC);

-- Seed templates
INSERT INTO notification_template (code, name, channel, subject_template, body_template) VALUES
('password_reset', 'Password Reset', 'email',
 'Password Reset Request',
 '<h2>Password Reset</h2><p>Your verification code is: <strong th:text="${code}"></strong></p><p>This code expires in 10 minutes.</p>'),
('account_created', 'Account Created', 'email',
 'Your Account Has Been Created',
 '<h2>Welcome</h2><p>Your account <strong th:text="${username}"></strong> has been created.</p><p>Please log in and change your password.</p>'),
('login_alert', 'Abnormal Login Alert', 'email',
 'Abnormal Login Detected',
 '<h2>Security Alert</h2><p>Your account was logged in from <strong th:text="${ip}"></strong> at <strong th:text="${time}"></strong>.</p>');
