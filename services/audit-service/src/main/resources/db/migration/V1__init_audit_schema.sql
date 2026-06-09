CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE audit_event (
    id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    principal_id VARCHAR(50),
    principal_name VARCHAR(100),
    target_type VARCHAR(50),
    target_id VARCHAR(100),
    detail JSONB DEFAULT '{}',
    result VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    error_detail TEXT,
    trace_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Create partitions for current month and next 2 months
CREATE TABLE audit_event_202606 PARTITION OF audit_event
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');

CREATE TABLE audit_event_202607 PARTITION OF audit_event
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');

CREATE TABLE audit_event_202608 PARTITION OF audit_event
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');

CREATE INDEX idx_audit_event_type ON audit_event(event_type);
CREATE INDEX idx_audit_principal ON audit_event(principal_name);
CREATE INDEX idx_audit_result ON audit_event(result);
CREATE INDEX idx_audit_created ON audit_event(created_at DESC);
