package com.company.auth.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditEventDto {
    private String id;
    private String eventType;
    private String principalName;
    private String targetType;
    private String targetId;
    private String detail;
    private String result;
    private String errorDetail;
    private String traceId;
    private LocalDateTime createdAt;
}
