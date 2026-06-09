package com.company.auth.common.constant;

public final class AuthConstants {
    private AuthConstants() {}

    public static final String TOKEN_TYPE = "Bearer ";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_SCOPE = "scope";

    // RabbitMQ queues
    public static final String QUEUE_APP_APPROVED = "app.approved";
    public static final String QUEUE_APP_REVOKED = "app.revoked";
    public static final String QUEUE_AUDIT_LOG = "audit.log";
    public static final String QUEUE_NOTIFY_EMAIL = "notify.email";
    public static final String QUEUE_NOTIFY_SMS = "notify.sms";
    public static final String QUEUE_NOTIFY_INAPP = "notify.inapp";
}
