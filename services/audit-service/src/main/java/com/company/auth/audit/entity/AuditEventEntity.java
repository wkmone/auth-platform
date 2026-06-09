package com.company.auth.audit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("audit_event")
public class AuditEventEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private String eventType;
    private String principalId;
    private String principalName;
    private String targetType;
    private String targetId;
    private String detail;      // JSONB stored as String
    private String result;
    private String errorDetail;
    private String traceId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
