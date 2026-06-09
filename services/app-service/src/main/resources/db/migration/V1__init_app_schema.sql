CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE oauth2_application (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    app_name VARCHAR(100) NOT NULL UNIQUE,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    grant_types VARCHAR(500) NOT NULL DEFAULT '["authorization_code","refresh_token"]',
    redirect_uris TEXT,
    scopes VARCHAR(500) NOT NULL DEFAULT '["openid","profile","email"]',
    access_token_ttl INT DEFAULT 900,
    require_consent BOOLEAN DEFAULT TRUE,
    owner VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 0
);

CREATE INDEX idx_app_status ON oauth2_application(status);
CREATE INDEX idx_app_client_id ON oauth2_application(client_id);
